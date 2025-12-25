package dev.slne.surf.playtime.paper.listener

import dev.slne.surf.playtime.paper.plugin
import dev.slne.surf.surfapi.core.api.util.mutableObject2BooleanMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObject2LongMapOf
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

object PlayerAfkListener : Listener {
    private val afkTime = 10.seconds.inWholeMilliseconds
    private val lastMovedTime = mutableObject2LongMapOf<UUID>()
    private val currentSentState = mutableObject2BooleanMapOf<UUID>()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!event.hasChangedOrientation()) {
            return
        }

        if (!event.hasChangedPosition()) {
            return
        }

        lastMovedTime[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        lastMovedTime[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId

        lastMovedTime.removeLong(uuid)
        currentSentState.removeBoolean(uuid)
    }

    fun afkCheckTask() {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin,{
            val currentTime = System.currentTimeMillis()

            lastMovedTime.object2LongEntrySet().fastForEach { entry ->
                val uuid = entry.key
                val lastMoved = entry.longValue
                val timeSinceLastMove = currentTime - lastMoved
                val isAfk = timeSinceLastMove >= afkTime
                val previousState = currentSentState.put(uuid, isAfk)
                if (previousState != isAfk) {
                    broadcastChange(uuid, isAfk)
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    private fun broadcastChange(uuid: UUID, isAfk: Boolean) {

    }
}