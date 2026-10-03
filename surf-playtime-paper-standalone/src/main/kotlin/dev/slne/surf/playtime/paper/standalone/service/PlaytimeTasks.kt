package dev.slne.surf.playtime.paper.standalone.service

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.paper.standalone.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

val playtimeTasks = PlaytimeTasks()

class PlaytimeTasks {
    private var playtimeTask: ScheduledTask? = null
    private var flushAllTask: ScheduledTask? = null

    fun startAll() {
        playtimeTask = playtimeTask()
        flushAllTask = flushAllTask()
    }

    fun stopAll() {
        playtimeTask?.cancel()
        flushAllTask?.cancel()
    }

    private fun playtimeTask() = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
        PlaytimeSessionService.updateAllActiveSessions()
    }, 0L, 1L, TimeUnit.SECONDS)

    private fun flushAllTask() = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
        plugin.launch {
            PlaytimeSessionService.flushAll()
        }
    }, 5L, 5L, TimeUnit.MINUTES)
}
