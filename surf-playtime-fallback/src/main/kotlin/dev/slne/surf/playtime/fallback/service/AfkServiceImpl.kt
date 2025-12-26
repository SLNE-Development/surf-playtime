package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.service.AfkService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(AfkService::class)
class AfkServiceImpl : AfkService, Services.Fallback {
    override val afkPlayers = mutableObjectSetOf<UUID>()

    override fun changeState(uuid: UUID, afk: Boolean): Boolean {
        return if (afk) {
            afkPlayers.add(uuid)
        } else {
            afkPlayers.remove(uuid)
        }
    }

    override fun isAfk(uuid: UUID) = afkPlayers.contains(uuid)
}