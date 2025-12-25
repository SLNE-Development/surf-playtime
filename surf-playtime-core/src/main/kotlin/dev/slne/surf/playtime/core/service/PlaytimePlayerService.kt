package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

val playtimePlayerService = requiredService<PlaytimePlayerService>()

interface PlaytimePlayerService {
    val onlinePlayers: ObjectSet<PlaytimePlayer>

    suspend fun loadPlayerByUuid(uuid: UUID): PlaytimePlayer?
    suspend fun loadPlayerByName(name: String): PlaytimePlayer?

    suspend fun getOrLoadPlayerByName(name: String): PlaytimePlayer?

    suspend fun savePlayer(playtimePlayer: PlaytimePlayer)
}