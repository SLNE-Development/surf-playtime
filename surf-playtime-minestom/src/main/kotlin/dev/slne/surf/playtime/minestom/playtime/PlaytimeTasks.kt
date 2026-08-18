package dev.slne.surf.playtime.minestom.playtime

import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.minestom.listener.checkAfkStates
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val playtimeTasks = PlaytimeTasks()

class PlaytimeTasks {
    private lateinit var playtimeTask: Job
    private lateinit var flushAllTask: Job
    private lateinit var afkCheckTask: Job

    fun startAll() {
        playtimeTask = playtimeTask()
        flushAllTask = flushAllTask()
        afkCheckTask = afkCheckTask()
    }

    fun stopAll() {
        playtimeTask.cancel()
        flushAllTask.cancel()
        afkCheckTask.cancel()
    }

    private fun playtimeTask() =
        minestomAsyncScope.runAtFixedRate(1.seconds, taskName = "playtime-update") {
            PlaytimeService.updateAllActiveSessions()
        }

    private fun flushAllTask() =
        minestomAsyncScope.runAtFixedRate(5.minutes, taskName = "playtime-flush") {
            PlaytimeService.flushAll()
        }

    private fun afkCheckTask() =
        minestomAsyncScope.runAtFixedRate(1.seconds, taskName = "afk-check") {
            checkAfkStates()
        }
}
