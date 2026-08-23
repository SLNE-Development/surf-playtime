package dev.slne.surf.playtime.microservice.repository

import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.microservice.expression.DurationSecondsExpression
import dev.slne.surf.playtime.microservice.table.PlaytimeSessionsTable
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
