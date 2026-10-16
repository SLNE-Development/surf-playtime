package dev.slne.surf.playtime.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.client.config.playtimeConfig
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.minestom.command.playtimeAdminCommand
import dev.slne.surf.playtime.minestom.command.playtimeCommand
import dev.slne.surf.playtime.minestom.listener.PlayerAfkListener
import dev.slne.surf.playtime.minestom.listener.PlayerConnectionListener
import dev.slne.surf.playtime.minestom.playtime.playtimeTasks

object PlaytimeMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        PayCheckService.create(playtimeConfig)

        ClientPlaytimeInstance.clientLoader.onLoad()

        playtimeTasks.startAll()
    }

    override suspend fun onEnable() {
        ClientPlaytimeInstance.clientLoader.onEnable()
        playtimeCommand()
        playtimeAdminCommand()

        PlayerConnectionListener.register(eventNode)
        PlayerAfkListener.register(eventNode)
    }

    override suspend fun onDisable() {
        playtimeTasks.stopAll()
        PlaytimeService.flushAll()

        ClientPlaytimeInstance.clientLoader.onDisable()
    }
}
