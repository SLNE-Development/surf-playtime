package dev.slne.surf.playtime.fallback.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.fallback.table.PlaytimePlayerTable
import dev.slne.surf.playtime.fallback.table.PlaytimeSessionsTable
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.util.*

val playtimeRepository = PlaytimeRepository()

class PlaytimeRepository {
    suspend fun saveSession(session: PlaytimeSession) = suspendTransaction {
        val playerId =
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq session.playerUuid)
                .firstOrNull()
                ?.get(PlaytimePlayerTable.id)?.value
                ?: return@suspendTransaction
        PlaytimeSessionsTable.upsert {
            it[sessionUuid] = session.sessionId
            it[this.playerId] = playerId
            it[serverName] = session.server
            it[category] = session.category
            it[durationSeconds] = session.durationSeconds
        }
    }

    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> = suspendTransaction {
        val playerId =
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq playerUuid)
                .firstOrNull()
                ?.get(PlaytimePlayerTable.id)?.value
                ?: return@suspendTransaction mutableObjectSetOf<PlaytimeSession>()
        PlaytimeSessionsTable.selectAll().where(PlaytimeSessionsTable.playerId eq playerId)
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.category],
                    durationSeconds = row[PlaytimeSessionsTable.durationSeconds]
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
        val playerId =
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq playerUuid)
                .firstOrNull()
                ?.get(PlaytimePlayerTable.id)?.value
                ?: return@suspendTransaction mutableObjectSetOf<PlaytimeSession>()
        PlaytimeSessionsTable.selectAll()
            .where { (PlaytimeSessionsTable.playerId eq playerId) and (PlaytimeSessionsTable.serverName eq serverName) }
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.category],
                    durationSeconds = row[PlaytimeSessionsTable.durationSeconds]
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
        val playerId =
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq playerUuid)
                .firstOrNull()
                ?.get(PlaytimePlayerTable.id)?.value
                ?: return@suspendTransaction mutableObjectSetOf<PlaytimeSession>()
        PlaytimeSessionsTable.selectAll()
            .where { (PlaytimeSessionsTable.playerId eq playerId) and (PlaytimeSessionsTable.category eq category) }
            .map { row ->
                PlaytimeSession(
                    sessionId = row[PlaytimeSessionsTable.sessionUuid],
                    playerUuid = playerUuid,
                    server = row[PlaytimeSessionsTable.serverName],
                    category = row[PlaytimeSessionsTable.category],
                    durationSeconds = row[PlaytimeSessionsTable.durationSeconds]
                )
            }.let { sessions ->
                val set = mutableObjectSetOf<PlaytimeSession>()
                set.addAll(sessions.toSet())
                set
            }
    }
}