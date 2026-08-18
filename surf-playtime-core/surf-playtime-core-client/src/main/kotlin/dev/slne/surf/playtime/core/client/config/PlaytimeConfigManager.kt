package dev.slne.surf.playtime.core.client.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.common.config.PlaytimeConfig

class PlaytimeConfigManager {
    private val configManager: SpongeConfigManager<PlaytimeConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            PlaytimeConfig::class.java,
            ClientPlaytimeInstance.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            PlaytimeConfig::class.java
        )
        reload()
    }

    fun edit(actions: PlaytimeConfig.() -> Unit) {
        configManager.config = configManager.config.apply { actions() }
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}

val playtimeConfigManager = PlaytimeConfigManager()
val playtimeConfig get() = playtimeConfigManager.config
