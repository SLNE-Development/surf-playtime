package dev.slne.surf.playtime.paper.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.playtime.core.common.config.PlaytimeConfig
import dev.slne.surf.playtime.paper.plugin

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