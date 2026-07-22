package dev.slne.surf.playtime.microservice.repository

import dev.slne.surf.api.core.util.toObjectSet
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.microservice.expression.DurationSecondsExpression
import dev.slne.surf.playtime.microservice.table.PlaytimeSessionsTable
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.time.temporal.ChronoUnit
import java.util.*

object PlaytimeRepository {
    suspend fun saveSession(session: PlaytimeSession) = suspendTransaction {
        if (session.startTime.until(session.endTime, ChronoUnit.MINUTES) < 1) {
            return@suspendTransaction false
        }

        PlaytimeSessionsTable.upsert(PlaytimeSessionsTable.sessionUuid) {
            it[sessionUuid] = session.sessionId
            it[this.playerUuid] = session.playerUuid
            it[serverName] = session.server
            it[serverCategory] = session.category
            it[startTime] = session.startTime
            it[endTime] = session.endTime
        }

        return@suspendTransaction true
    }

    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> = suspendTransaction {
        PlaytimeSessionsTable.selectAll().where(PlaytimeSessionsTable.playerUuid eq playerUuid)
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.serverCategory],
                    startTime = row[PlaytimeSessionsTable.startTime],
                    endTime = row[PlaytimeSessionsTable.endTime],
                )
            }.toSet().toObjectSet()
    }

    suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = suspendTransaction {
        PlaytimeSessionsTable.selectAll()
            .where { (PlaytimeSessionsTable.playerUuid eq playerUuid) and (PlaytimeSessionsTable.serverName eq serverName) }
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.serverCategory],
                    startTime = row[PlaytimeSessionsTable.startTime],
                    endTime = row[PlaytimeSessionsTable.endTime],
                )
            }.toSet().toObjectSet()
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
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.serverCategory],
                    startTime = row[PlaytimeSessionsTable.startTime],
                    endTime = row[PlaytimeSessionsTable.endTime],
                )
            }.toSet().toObjectSet()
    }
}