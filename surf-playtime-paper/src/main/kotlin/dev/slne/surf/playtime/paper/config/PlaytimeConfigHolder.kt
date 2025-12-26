package dev.slne.surf.playtime.paper.config

import dev.slne.surf.playtime.paper.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class PlaytimeConfigHolder {
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

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}