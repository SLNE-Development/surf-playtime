package dev.slne.surf.playtime.paper.playtime

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

val playtimeTasks = PlaytimeTasks()

class PlaytimeTasks {
    lateinit var playtimeTask: ScheduledTask
    lateinit var flushAllTask: ScheduledTask

    fun startAll() {
        playtimeTask = playTimeTask()
        flushAllTask = flushAllTask()
    }

    fun stopAll() {
        playtimeTask.cancel()
        flushAllTask.cancel()
    }

    private fun playTimeTask() = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
        playtimeService.updateAllActiveSessions()
    }, 0L, 1L, TimeUnit.SECONDS)

    private fun flushAllTask() = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
        plugin.launch {
            playtimeService.flushAll()
        }
    }, 0L, 5L, TimeUnit.MINUTES)
}