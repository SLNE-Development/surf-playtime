package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import java.util.*

object AfkService {
    val afkPlayers = mutableObjectSetOf<UUID>()

    fun changeState(uuid: UUID, afk: Boolean): Boolean {
        return if (afk) {
            afkPlayers.add(uuid)
        } else {
            afkPlayers.remove(uuid)
        }
    }

    fun isAfk(uuid: UUID) = afkPlayers.contains(uuid)
}