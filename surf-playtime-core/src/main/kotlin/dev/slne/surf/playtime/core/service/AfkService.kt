package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

val afkService = requiredService<AfkService>()

interface AfkService {
    val afkPlayers: ObjectSet<PlaytimePlayer>

    fun isAfk(uuid: UUID): Boolean
}