package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

object PlayerAfkListener : Listener {
    private val afkTime = 3.minutes.inWholeMilliseconds

    private val lastMovedTime = ConcurrentHashMap<UUID, Long>()
    private val currentSentState = ConcurrentHashMap<UUID, Boolean>()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!event.hasChangedOrientation()) return
        lastMovedTime[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        lastMovedTime[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        lastMovedTime.remove(event.player.uniqueId)
        currentSentState.remove(event.player.uniqueId)
    }

    fun afkCheckTask() {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            val currentTime = System.currentTimeMillis()

            lastMovedTime.forEach { (uuid, lastMoved) ->
                val isAfk = currentTime - lastMoved >= afkTime
                val previousState = currentSentState.put(uuid, isAfk)

                if (previousState == null || previousState != isAfk) {
                    Bukkit.getScheduler().run {
                        broadcastChange(uuid, isAfk)
                    }
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    private fun broadcastChange(uuid: UUID, isAfk: Boolean) {
        AfkService.changeState(uuid, isAfk)

        val now = LocalDateTime.now()

        if (isAfk) {
            val activeSessions = PlaytimeService.activePlaytimeSessions
                .filter { it.playerUuid == uuid }

            activeSessions.forEach { session ->
                session.endTime = now
                PlaytimeService.removeCachedSession(session.sessionId)

                plugin.launch {
                    PlaytimeService.saveSession(session)
                }
            }
        } else {
            val activeSession = PlaytimeService.activePlaytimeSessions
                .find { it.playerUuid == uuid }

            if (activeSession == null) {
                PlaytimeService.cacheSession(
                    PlaytimeSession(
                        uuid,
                        UUID.randomUUID(),
                        SurfCoreApi.getCurrentServerDisplayName(),
                        SurfCoreApi.getCurrentServerCategory(),
                        now,
                        now
                    )
                )
            }
        }

        Bukkit.getGlobalRegionScheduler().run(plugin) {
            AfkStateChangeEvent(uuid, isAfk).callEvent()
        }

        Bukkit.getPlayer(uuid)?.sendText {
            appendInfoPrefix()
            info("Du bist nun ")
            if (isAfk) info("AFK.") else info("nicht mehr AFK.")
        }
    }
}
