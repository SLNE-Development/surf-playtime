package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val playtimeService = requiredService<PlaytimeService>()

interface PlaytimeService {
    val activePlaytimeSessions: ObjectSet<PlaytimeSession>

    suspend fun saveSession(session: PlaytimeSession)
    suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession>

    fun increaseAllSessions()

    suspend fun flushAll() {
        for (session in activePlaytimeSessions) {
            saveSession(session)
        }
    }
}