package dev.slne.surf.playtime.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.playtime.core.databaseBridge
import dev.slne.surf.playtime.core.service.payCheckService
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.command.playtimeAdminCommand
import dev.slne.surf.playtime.paper.command.playtimeCommand
import dev.slne.surf.playtime.paper.config.PlaytimeConfigManager
import dev.slne.surf.playtime.paper.listener.PlayerAfkListener
import dev.slne.surf.playtime.paper.listener.PlayerJoinListener
import dev.slne.surf.playtime.paper.listener.PlayerQuitListener
import dev.slne.surf.playtime.paper.playtime.playtimeTasks
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.bukkit.api.extensions.pluginManager
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        payCheckService.create(playtimeConfig)

        PlayerJoinListener.register()
        PlayerQuitListener.register()
        PlayerAfkListener.register()
        PlayerAfkListener.afkCheckTask()
        playtimeTasks.startAll()

        runBlocking {
            databaseBridge.initialize(dataPath)
        }

        playtimeCommand()
        playtimeAdminCommand()
    }

    override fun onDisable() {
        playtimeTasks.stopAll()

        runBlocking {
            playtimeService.flushAll()
        }

        databaseBridge.disconnect()
    }
}

val hasTransactionHook get() = pluginManager.isPluginEnabled("surf-transaction-paper")

val playtimeConfigManager = PlaytimeConfigManager()
val playtimeConfig get() = playtimeConfigManager.config