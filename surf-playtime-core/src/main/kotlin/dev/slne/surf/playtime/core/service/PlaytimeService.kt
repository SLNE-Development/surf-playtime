package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.time.LocalDateTime
import java.util.*

val playtimeService = requiredService<PlaytimeService>()

interface PlaytimeService {
    val activePlaytimeSessions: ObjectSet<PlaytimeSession>

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

    suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession>

    suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession>

    suspend fun flushAll() {
        for (session in activePlaytimeSessions) {
            saveSession(session.apply {
                endTime = LocalDateTime.now()
            })
        }
    }

    fun updateAllActiveSessions() {
        val now = LocalDateTime.now()

        activePlaytimeSessions.forEach {
            it.endTime = now
        }
    }
}