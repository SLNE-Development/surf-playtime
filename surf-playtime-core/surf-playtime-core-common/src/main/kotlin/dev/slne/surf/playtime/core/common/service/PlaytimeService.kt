package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.time.LocalDateTime
import java.util.*

private val service = requiredService<PlaytimeService>()

private const val MAX_CONCURRENT_FLUSHES = 32

interface PlaytimeService {
    /**
     * Snapshot of every session currently tracked on this server.
     */
    val activePlaytimeSessions: Set<PlaytimeSession>

    /**
     * Live view of every session currently tracked on this server.
     *
     * Iterating this does not copy, but it is only weakly consistent with concurrent changes.
     * Use [activePlaytimeSessions] when a stable snapshot is required.
     */
    val activeSessionsView: Collection<PlaytimeSession>

    /**
     * The sessions [playerUuid] currently has on this server.
     */
    fun activeSessionsOf(playerUuid: UUID): List<PlaytimeSession>

    suspend fun saveSession(session: PlaytimeSession)
    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession>
    suspend fun getAndLoadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> {
        val activeSession = activeSessionsOf(playerUuid).firstOrNull()
        val loadedSessions = loadSessions(playerUuid)

        val result = mutableObjectSetOf<PlaytimeSession>(loadedSessions.size + 1)

        if (activeSession != null) {
            result.add(activeSession)

            for (session in loadedSessions) {
                if (session.sessionId != activeSession.sessionId) {
                    result.add(session)
                }
            }
        } else {
            result.addAll(loadedSessions)
        }

        return result
    }

    fun cacheSession(session: PlaytimeSession)
    fun removeCachedSession(sessionId: UUID)

    suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession>

    suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession>

    suspend fun flushAll(): Unit = coroutineScope {
        val sessions = activePlaytimeSessions

        if (sessions.isEmpty()) {
            return@coroutineScope
        }

        val semaphore = Semaphore(MAX_CONCURRENT_FLUSHES)

        for (session in sessions) {
            launch {
                semaphore.withPermit {
                    saveSession(session.apply { endTime = LocalDateTime.now() })
                }
            }
        }
    }

    suspend fun updateAllActiveSessions() {
        val now = LocalDateTime.now()

        for (session in activeSessionsView) {
            session.endTime = now

            val playerUuid = session.playerUuid
            if (!AfkService.isAfk(playerUuid)) {
                PayCheckService.increasePlaytime(playerUuid, 1)
            }
        }
    }

    companion object : PlaytimeService by service
}
