package dev.slne.surf.playtime.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val afkService = requiredService<AfkService>()

interface AfkService {
    val afkPlayers: ObjectSet<UUID>

    fun changeState(uuid: UUID, afk: Boolean): Boolean

    fun isAfk(uuid: UUID): Boolean
}