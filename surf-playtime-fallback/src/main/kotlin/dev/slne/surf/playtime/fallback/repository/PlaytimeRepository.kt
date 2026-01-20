package dev.slne.surf.playtime.fallback.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.fallback.table.PlaytimeSessionsTable
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.util.*

val playtimeRepository = PlaytimeRepository()

class PlaytimeRepository {
    suspend fun saveSession(session: PlaytimeSession) = suspendTransaction {
        PlaytimeSessionsTable.upsert {
            it[sessionUuid] = session.sessionId
            it[this.playerUuid] = session.playerUuid
            it[serverName] = session.server
            it[serverCategory] = session.category
            it[startTime] = session.startTime
            it[endTime] = session.endTime
        }
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
            }.let { sessions ->
                val set = mutableObjectSetOf<PlaytimeSession>()
                set.addAll(sessions.toSet())
                set
            }
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
            }.let { sessions ->
                val set = mutableObjectSetOf<PlaytimeSession>()
                set.addAll(sessions.toSet())
                set
            }
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
            }.let { sessions ->
                val set = mutableObjectSetOf<PlaytimeSession>()
                set.addAll(sessions.toSet())
                set
            }
    }
}