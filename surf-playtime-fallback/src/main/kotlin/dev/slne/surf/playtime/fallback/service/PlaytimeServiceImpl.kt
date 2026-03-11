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
    override val activePlaytimeSessions = ConcurrentSet<PlaytimeSession>()
    override suspend fun saveSession(session: PlaytimeSession) {
        playtimeRepository.saveSession(session)
    }

    override suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        playtimeRepository.loadSessions(playerUuid)

    override suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = playtimeRepository.loadSessionsByServer(playerUuid, serverName)

    override suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession> = playtimeRepository.loadSessionsByCategory(playerUuid, category)
}