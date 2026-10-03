package dev.slne.surf.playtime.paper.standalone.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.playtime.paper.standalone.plugin

class StandalonePlaytimeConfigManager {
    private val configManager: SpongeConfigManager<StandalonePlaytimeConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            StandalonePlaytimeConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            StandalonePlaytimeConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}

val playtimeConfigManager by lazy { StandalonePlaytimeConfigManager() }
val playtimeConfig get() = playtimeConfigManager.config
