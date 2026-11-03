package dev.slne.surf.playtime.database.repository

import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Op
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.greater
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.inList
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.less
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.sum
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.CustomDateTimeFunction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.dateTimeParam
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.database.table.PlaytimeSessionsTable
import dev.slne.surf.playtime.database.table.exp.DurationSecondsExpression
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongMaps
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import java.time.temporal.ChronoUnit
import java.util.*

object PlaytimeRepository {
    suspend fun saveSession(session: PlaytimeSession): Boolean {
        if (session.startTime.until(session.endTime, ChronoUnit.MINUTES) < 1) {
            return false
        }

        suspendTransaction {
            PlaytimeSessionsTable.upsert(PlaytimeSessionsTable.sessionUuid) {
                it[sessionUuid] = session.sessionId
                it[this.playerUuid] = session.playerUuid
                it[serverName] = session.server
                it[serverCategory] = session.category
                it[startTime] = session.startTime
                it[endTime] = session.endTime
            }
        }

        return true
    }

    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> = suspendTransaction {
        PlaytimeSessionsTable.selectAll().where(PlaytimeSessionsTable.playerUuid eq playerUuid)
            .map { row -> row.toPlaytimeSession(playerUuid) }
            .collectToSessions()
    }

    suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = suspendTransaction {
        PlaytimeSessionsTable.selectAll()
            .where { (PlaytimeSessionsTable.playerUuid eq playerUuid) and (PlaytimeSessionsTable.serverName eq serverName) }
            .map { row -> row.toPlaytimeSession(playerUuid) }
            .collectToSessions()
    }

    suspend fun loadPlaytimeSecondsByServer(
        playerUuid: UUID,
        serverName: String
    ): Long = suspendTransaction {
        val durationSeconds = DurationSecondsExpression(
            start = PlaytimeSessionsTable.startTime,
            end = PlaytimeSessionsTable.endTime
        )
        val totalSeconds = durationSeconds.sum()

        PlaytimeSessionsTable
            .select(totalSeconds)
            .where {
                (PlaytimeSessionsTable.playerUuid eq playerUuid) and
                        (PlaytimeSessionsTable.serverName eq serverName)
            }
            .firstOrNull()
            ?.get(totalSeconds)
            ?: 0L
    }


    suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession> = suspendTransaction {
        PlaytimeSessionsTable.selectAll()
            .where { (PlaytimeSessionsTable.playerUuid eq playerUuid) and (PlaytimeSessionsTable.serverCategory eq category) }
            .map { row -> row.toPlaytimeSession(playerUuid) }
            .collectToSessions()
    }

    /**
     * Sums the stored playtime per player of all sessions matching [filter], counting only the
     * whole seconds of each session inside the window of [filter]. The database clips and sums
     * the sessions, so only one row per player is transferred.
     */
    suspend fun loadPlaytimePerPlayer(filter: PlaytimeFilter): Object2LongMap<UUID> {
        if (filter.matchesNothing()) {
            return Object2LongMaps.emptyMap()
        }

        val totalSeconds = DurationSecondsExpression(
            start = CustomDateTimeFunction(
                "GREATEST",
                PlaytimeSessionsTable.startTime,
                dateTimeParam(filter.from)
            ),
            end = CustomDateTimeFunction(
                "LEAST",
                PlaytimeSessionsTable.endTime,
                dateTimeParam(filter.to)
            )
        ).sum()

        return suspendTransaction {
            val playtime = Object2LongOpenHashMap<UUID>()

            PlaytimeSessionsTable
                .select(PlaytimeSessionsTable.playerUuid, totalSeconds)
                .where(filter.toCondition())
                .groupBy(PlaytimeSessionsTable.playerUuid)
                .collect { row ->
                    val seconds = row[totalSeconds] ?: 0

                    if (seconds > 0) {
                        playtime.put(row[PlaytimeSessionsTable.playerUuid], seconds)
                    }
                }

            Object2LongMaps.unmodifiable(playtime)
        }
    }

    private fun PlaytimeFilter.toCondition(): Op<Boolean> {
        var condition: Op<Boolean> =
            (PlaytimeSessionsTable.startTime less to) and (PlaytimeSessionsTable.endTime greater from)

        servers?.let { condition = condition and (PlaytimeSessionsTable.serverName inList it) }
        categories?.let { condition = condition and (PlaytimeSessionsTable.serverCategory inList it) }
        players?.let { condition = condition and (PlaytimeSessionsTable.playerUuid inList it) }

        return condition
    }

    private fun ResultRow.toPlaytimeSession(playerUuid: UUID) = PlaytimeSession(
        sessionId = this[PlaytimeSessionsTable.sessionUuid],
        playerUuid = playerUuid,
        server = this[PlaytimeSessionsTable.serverName],
        category = this[PlaytimeSessionsTable.serverCategory],
        startTime = this[PlaytimeSessionsTable.startTime],
        endTime = this[PlaytimeSessionsTable.endTime],
    )

    private suspend fun Flow<PlaytimeSession>.collectToSessions(): ObjectSet<PlaytimeSession> =
        toCollection(mutableObjectSetOf()).freeze()
}
