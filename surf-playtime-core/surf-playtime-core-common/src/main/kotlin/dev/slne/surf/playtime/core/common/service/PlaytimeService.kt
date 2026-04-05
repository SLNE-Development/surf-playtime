package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.time.LocalDateTime
import java.util.*

private val service = requiredService<PlaytimeService>()

interface PlaytimeService {
    val activePlaytimeSessions: Set<PlaytimeSession>

    suspend fun saveSession(session: PlaytimeSession)
    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession>
    suspend fun getAndLoadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> {
        val activeSession = activePlaytimeSessions.find { it.playerUuid == playerUuid }
        val loadedSessions = loadSessions(playerUuid)

        val result = mutableObjectSetOf<PlaytimeSession>()

        if (activeSession != null) {
            result.add(activeSession)
            result.addAll(loadedSessions.filterNot { it.sessionId == activeSession.sessionId })
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

    suspend fun flushAll() {
        activePlaytimeSessions.toSet().forEach {
            saveSession(it.apply {
                endTime = LocalDateTime.now()
            })
        }
    }

    suspend fun updateAllActiveSessions() {
        val now = LocalDateTime.now()

        activePlaytimeSessions.forEach {
            it.endTime = now

            if (!AfkService.isAfk(it.playerUuid)) {
                PayCheckService.increasePlaytime(it.playerUuid, 1)
            }
        }
    }

    companion object : PlaytimeService by service
}