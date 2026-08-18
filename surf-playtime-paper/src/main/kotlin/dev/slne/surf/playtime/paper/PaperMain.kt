package dev.slne.surf.playtime.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.client.config.playtimeConfig
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.paper.command.playtimeAdminCommand
import dev.slne.surf.playtime.paper.command.playtimeCommand
import dev.slne.surf.playtime.paper.listener.PlayerAfkListener
import dev.slne.surf.playtime.paper.listener.PlayerJoinListener
import dev.slne.surf.playtime.paper.listener.PlayerQuitListener
import dev.slne.surf.playtime.paper.playtime.playtimeTasks
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        ClientPlaytimeInstance.clientLoader.onLoad()
    }

    override suspend fun onEnableAsync() {
        ClientPlaytimeInstance.clientLoader.onEnable()

        PayCheckService.create(playtimeConfig)

        PlayerJoinListener.register()
        PlayerQuitListener.register()
        PlayerAfkListener.register()
        PlayerAfkListener.startAfkCheckTask()
        playtimeTasks.startAll()

        playtimeCommand()
        playtimeAdminCommand()
    }

    override suspend fun onDisableAsync() {
        playtimeTasks.stopAll()
        PlaytimeService.flushAll()

        ClientPlaytimeInstance.clientLoader.onDisable()
    }
}

val hasTransactionHook get() = pluginManager.isPluginEnabled("surf-transaction-paper")
