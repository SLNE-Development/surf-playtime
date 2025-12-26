package dev.slne.surf.playtime.fallback.repository

import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.fallback.table.PlaytimePlayerTable
import dev.slne.surf.playtime.fallback.table.PlaytimeSessionsTable
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

val playtimeRepository = PlaytimeRepository()

class PlaytimeRepository {
    suspend fun saveSession(session: PlaytimeSession) = newSuspendedTransaction(Dispatchers.IO) {
        val playerId =
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq session.playerUuid)
                .firstOrNull()
                ?.get(PlaytimePlayerTable.id)?.value
                ?: return@newSuspendedTransaction
        PlaytimeSessionsTable.upsert(PlaytimeSessionsTable.sessionUuid) {
            it[sessionUuid] = session.sessionId
            it[this.playerId] = playerId
            it[serverName] = session.server
            it[category] = session.category
            it[durationSeconds] = session.durationSeconds
        }
    }

    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        newSuspendedTransaction(
            Dispatchers.IO
        ) {
            val playerId =
                PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq playerUuid)
                    .firstOrNull()
                    ?.get(PlaytimePlayerTable.id)?.value
                    ?: return@newSuspendedTransaction mutableObjectSetOf<PlaytimeSession>()
            PlaytimeSessionsTable.selectAll().where(PlaytimeSessionsTable.playerId eq playerId)
                .map { row ->
                    PlaytimeSession(
                        sessionId = row[PlaytimeSessionsTable.sessionUuid],
                        playerUuid = playerUuid,
                        server = row[PlaytimeSessionsTable.serverName],
                        category = row[PlaytimeSessionsTable.category],
                        durationSeconds = row[PlaytimeSessionsTable.durationSeconds]
                    )
                }
        }.let { sessions ->
            val set = mutableObjectSetOf<PlaytimeSession>()
            set.addAll(sessions)
            set
        }
}