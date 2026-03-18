package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.PlaytimeService
import dev.slne.surf.playtime.fallback.repository.playtimeRepository
import io.ktor.util.collections.*
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(PlaytimeService::class)
class PlaytimeServiceImpl : PlaytimeService, Services.Fallback {
    private val _sessions = ConcurrentMap<UUID, PlaytimeSession>()

    override val activePlaytimeSessions get() = _sessions.values.toSet()
    override suspend fun saveSession(session: PlaytimeSession) {
        playtimeRepository.saveSession(session)
        cacheSession(session)
    }


    override suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        playtimeRepository.loadSessions(playerUuid)

    override fun cacheSession(session: PlaytimeSession) {
        _sessions[session.sessionId] = session
    }

    override fun removeCachedSession(sessionId: UUID) {
        _sessions.remove(sessionId)
    }

    override suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = playtimeRepository.loadSessionsByServer(playerUuid, serverName)

    override suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession> = playtimeRepository.loadSessionsByCategory(playerUuid, category)
}