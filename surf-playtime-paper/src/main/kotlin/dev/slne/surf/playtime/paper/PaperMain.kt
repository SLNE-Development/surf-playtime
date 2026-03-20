package dev.slne.surf.playtime.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.playtime.core.common.service.payCheckService
import dev.slne.surf.playtime.core.common.service.playtimeService
import dev.slne.surf.playtime.core.paper.PaperPlaytimeInstance
import dev.slne.surf.playtime.paper.command.playtimeAdminCommand
import dev.slne.surf.playtime.paper.command.playtimeCommand
import dev.slne.surf.playtime.paper.config.PlaytimeConfigManager
import dev.slne.surf.playtime.paper.listener.PlayerAfkListener
import dev.slne.surf.playtime.paper.listener.PlayerJoinListener
import dev.slne.surf.playtime.paper.listener.PlayerQuitListener
import dev.slne.surf.playtime.paper.playtime.playtimeTasks
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        PaperPlaytimeInstance.paperLoader.onLoad()
    }

    override suspend fun onEnableAsync() {
        PaperPlaytimeInstance.paperLoader.onEnable()

        payCheckService.create(playtimeConfig)

        PlayerJoinListener.register()
        PlayerQuitListener.register()
        PlayerAfkListener.register()
        PlayerAfkListener.afkCheckTask()
        playtimeTasks.startAll()

        playtimeCommand()
        playtimeAdminCommand()
    }

    override suspend fun onDisableAsync() {
        playtimeTasks.stopAll()
        playtimeService.flushAll()

        PaperPlaytimeInstance.paperLoader.onDisable()
    }
}

val hasTransactionHook get() = pluginManager.isPluginEnabled("surf-transaction-paper")

val playtimeConfigManager = PlaytimeConfigManager()
val playtimeConfig get() = playtimeConfigManager.config