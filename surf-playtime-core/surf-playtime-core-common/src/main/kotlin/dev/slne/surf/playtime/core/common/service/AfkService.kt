package dev.slne.surf.playtime.core.common.service

import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AfkService {
    private val afkPlayers = ConcurrentHashMap.newKeySet<UUID>()

    fun changeState(uuid: UUID, afk: Boolean): Boolean {
        return if (afk) {
            afkPlayers.add(uuid)
        } else {
            afkPlayers.remove(uuid)
        }
    }

    fun isAfk(uuid: UUID) = afkPlayers.contains(uuid)
}