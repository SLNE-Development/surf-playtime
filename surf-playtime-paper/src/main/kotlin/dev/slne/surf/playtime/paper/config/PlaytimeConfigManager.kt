package dev.slne.surf.playtime.paper.config

import dev.slne.surf.playtime.core.config.PlaytimeConfig
import dev.slne.surf.playtime.paper.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class PlaytimeConfigManager {
    private val configManager: SpongeConfigManager<PlaytimeConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            PlaytimeConfig::class.java,
            plugin.dataPath,
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