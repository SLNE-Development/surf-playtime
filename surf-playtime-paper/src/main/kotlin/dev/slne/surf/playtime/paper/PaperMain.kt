package dev.slne.surf.playtime.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.playtime.core.databaseBridge
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.command.playtimeCommand
import dev.slne.surf.playtime.paper.config.PlaytimeConfigHolder
import dev.slne.surf.playtime.paper.listener.PlayerAfkListener
import dev.slne.surf.playtime.paper.listener.PlayerJoinListener
import dev.slne.surf.playtime.paper.listener.PlayerQuitListener
import dev.slne.surf.playtime.paper.playtime.playtimeTasks
import dev.slne.surf.surfapi.bukkit.api.event.register
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        PlayerJoinListener.register()
        PlayerQuitListener.register()
        PlayerAfkListener.register()
        PlayerAfkListener.afkCheckTask()
        playtimeTasks.startAll()

        redisLoader.connect()
        databaseBridge.initialize(dataPath)

        playtimeCommand()
    }

    override fun onDisable() {
        playtimeTasks.stopAll()

        runBlocking {
            playtimeService.flushAll()
        }

        redisLoader.disconnect()
        databaseBridge.disconnect()
    }
}

val playtimeConfigHolder = PlaytimeConfigHolder()
val playtimeConfig get() = playtimeConfigHolder.config