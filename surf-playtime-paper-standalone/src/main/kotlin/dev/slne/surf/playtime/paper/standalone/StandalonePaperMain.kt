package dev.slne.surf.playtime.paper.standalone

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.playtime.database.table.PlaytimeSessionsTable
import dev.slne.surf.playtime.paper.standalone.afk.AfkTracker
import dev.slne.surf.playtime.paper.standalone.command.playtimeAdminCommand
import dev.slne.surf.playtime.paper.standalone.command.playtimeCommand
import dev.slne.surf.playtime.paper.standalone.config.playtimeConfigManager
import dev.slne.surf.playtime.paper.standalone.listener.PlayerAfkListener
import dev.slne.surf.playtime.paper.standalone.listener.PlayerConnectionListener
import dev.slne.surf.playtime.paper.standalone.service.PlaytimeSessionService
import dev.slne.surf.playtime.paper.standalone.service.playtimeTasks
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(StandalonePaperMain::class.java)

class StandalonePaperMain : SuspendingJavaPlugin() {
    lateinit var databaseApi: DatabaseApi
        private set

    override suspend fun onLoadAsync() {
        playtimeConfigManager.reload()
        databaseApi = DatabaseApi.create(dataPath)
    }

    override suspend fun onEnableAsync() {
        suspendTransaction {
            SchemaUtils.create(PlaytimeSessionsTable)
        }

        PlayerConnectionListener.register()
        PlayerAfkListener.register()
        PlayerAfkListener.startAfkCheckTask()

        Bukkit.getOnlinePlayers().forEach {
            PlaytimeSessionService.startSession(it.uniqueId)
            AfkTracker.track(it.uniqueId)
        }

        playtimeTasks.startAll()

        playtimeCommand()
        playtimeAdminCommand()
    }

    override suspend fun onDisableAsync() {
        playtimeTasks.stopAll()
        PlaytimeSessionService.flushAll()

        databaseApi.shutdown()
    }
}
