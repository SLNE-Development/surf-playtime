package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.PlaytimeService
import dev.slne.surf.playtime.core.service.afkService
import dev.slne.surf.playtime.fallback.repository.playtimeRepository
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(PlaytimeService::class)
class PlaytimeServiceImpl : PlaytimeService, Services.Fallback {
    override val activePlaytimeSessions = mutableObjectSetOf<PlaytimeSession>()
    override suspend fun saveSession(session: PlaytimeSession) =
        playtimeRepository.saveSession(session)

    override suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        playtimeRepository.loadSessions(playerUuid)

    override fun increaseAllSessions() {
        activePlaytimeSessions.filterNot { afkService.isAfk(it.playerUuid) }.forEach {
            it.increasePlaytime()
        }
    }
}