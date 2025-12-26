package dev.slne.surf.playtime.paper.listener

import dev.slne.surf.playtime.api.redis.event.AfkStateChangeRedisEvent
import dev.slne.surf.playtime.core.service.afkService
import dev.slne.surf.playtime.paper.plugin
import dev.slne.surf.playtime.paper.redisApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2BooleanMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObject2LongMapOf
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

object PlayerAfkListener : Listener {
    private val afkTime = 3.minutes.inWholeMilliseconds
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
        plugin.logger.info("Starting Afk check task...")
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
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
        afkService.changeState(uuid, isAfk)

        Bukkit.getPlayer(uuid)?.sendText {
            appendPrefix()
            info("Du bist nun ")
            if (isAfk) {
                info("Afk.")
            } else {
                info("nicht mehr Afk.")
            }
        }

        redisApi.publishEvent(
            AfkStateChangeRedisEvent(
                uuid = uuid,
                isAfk = isAfk
            )
        )
    }
}