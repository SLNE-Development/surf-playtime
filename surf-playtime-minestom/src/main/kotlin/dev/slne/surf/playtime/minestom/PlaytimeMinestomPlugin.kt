package dev.slne.surf.playtime.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.playtime.minestom.command.PlaytimeCommandRegistrar
import dev.slne.surf.playtime.minestom.listener.PlayerAfkListener
import dev.slne.surf.playtime.minestom.listener.PlayerConnectionListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-playtime-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom"
    ]
)
class PlaytimeMinestomPlugin : MinestomPlugin(PlaytimeMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindCommandRegistrar<PlaytimeCommandRegistrar>()
        bindEventRegistrar<PlayerConnectionListener>()
        bindEventRegistrar<PlayerAfkListener>()
    }
}
