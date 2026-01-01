package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.core.service.PlaytimePlayerService
import dev.slne.surf.playtime.fallback.repository.playtimePlayerRepository
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(PlaytimePlayerService::class)
class PlaytimePlayerServiceImpl : PlaytimePlayerService, Services.Fallback {
    override val onlinePlayers = mutableObjectSetOf<PlaytimePlayer>()

    override suspend fun loadPlayerByUuid(uuid: UUID) =
        playtimePlayerRepository.loadPlayerByUuid(uuid)

    override suspend fun loadPlayerByName(name: String) =
        playtimePlayerRepository.loadPlayerByName(name)

    override suspend fun getOrLoadPlayerByName(name: String) =
        getPlayer(name) ?: loadPlayerByName(name)

    override suspend fun savePlayer(playtimePlayer: PlaytimePlayer) {
        playtimePlayerRepository.savePlayer(playtimePlayer)
    }
}