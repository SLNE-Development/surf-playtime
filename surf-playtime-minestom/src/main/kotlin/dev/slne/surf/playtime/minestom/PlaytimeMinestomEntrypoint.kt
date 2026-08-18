package dev.slne.surf.playtime.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.client.config.playtimeConfig
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.minestom.playtime.playtimeTasks
import java.nio.file.Path

@Singleton
class PlaytimeMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        PayCheckService.create(playtimeConfig)

        ClientPlaytimeInstance.clientLoader.onLoad()
        ClientPlaytimeInstance.clientLoader.onEnable()

        playtimeTasks.startAll()
    }

    override suspend fun stop() {
        playtimeTasks.stopAll()
        PlaytimeService.flushAll()

        ClientPlaytimeInstance.clientLoader.onDisable()
    }

    companion object {
        lateinit var dataPath: Path
    }
}
