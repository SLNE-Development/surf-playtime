package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object PlayerAfkListener : Listener {
    private val afkTimeNanos = 3.minutes.inWholeNanoseconds

    private data class AfkState(
        val lastActivityNanos: Long,
        val isAfk: Boolean,
    )

    private val states = ConcurrentHashMap<UUID, AfkState>()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!event.hasChangedOrientation()) return
        markActive(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        states[player.uniqueId] = AfkState(
            lastActivityNanos = System.nanoTime(),
            isAfk = false,
        )

        AfkService.changeState(player.uniqueId, false)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        states.remove(event.player.uniqueId)
    }

    fun startAfkCheckTask() {
        plugin.scope.runAtFixedRate(1.seconds, taskName = "afk-check") {
            checkAfkStates()
        }
    }

    private fun markActive(player: Player) {
        val uuid = player.uniqueId
        val now = System.nanoTime()
        var becameActive = false

        states.compute(uuid) { _, previous ->
            becameActive = previous?.isAfk == true

            AfkState(
                lastActivityNanos = now,
                isAfk = false,
            )
        }

        if (becameActive) {
            applyAfkState(player, false)
        }
    }

    private fun checkAfkStates() {
        val now = System.nanoTime()

        for ((uuid, state) in states) {
            if (state.isAfk || now - state.lastActivityNanos < afkTimeNanos) continue
            var becameAfk = false

            states.computeIfPresent(uuid) { _, current ->
                // Recheck — player may have moved by now
                if (current.isAfk || now - current.lastActivityNanos < afkTimeNanos) {
                    current
                } else {
                    becameAfk = true
                    current.copy(isAfk = true)
                }
            }

            if (!becameAfk) continue

            val player = Bukkit.getPlayer(uuid)
            if (player == null) {
                states.remove(uuid)
                continue
            }

            plugin.launch(plugin.entityDispatcher(player)) {
                if (player.isOnline) {
                    applyAfkState(player, true)
                }
            }
        }
    }

    private fun applyAfkState(player: Player, isAfk: Boolean) {
        val uuid = player.uniqueId

        // Check that the state is still the same, as it may have changed since the task was scheduled
        if (states[uuid]?.isAfk != isAfk) return

        AfkService.changeState(uuid, isAfk)
        updatePlaytimeSession(uuid, isAfk)

        plugin.launch(plugin.globalRegionDispatcher) {
            AfkStateChangeEvent(uuid, isAfk).callEvent()
        }

        player.sendText {
            appendInfoPrefix()
            info("Du bist nun ")

            if (isAfk) {
                info("AFK.")
            } else {
                info("nicht mehr AFK.")
            }
        }
    }

    private fun updatePlaytimeSession(uuid: UUID, isAfk: Boolean) {
        val now = LocalDateTime.now()
        val activeSessions = PlaytimeService.activePlaytimeSessions
            .filter { it.playerUuid == uuid }

        if (isAfk) {
            activeSessions.forEach { session ->
                session.endTime = now
                PlaytimeService.removeCachedSession(session.sessionId)

                plugin.launch {
                    PlaytimeService.saveSession(session)
                }
            }

            return
        }

        if (activeSessions.isEmpty()) {
            PlaytimeService.cacheSession(
                PlaytimeSession(
                    playerUuid = uuid,
                    sessionId = UUID.randomUUID(),
                    server = SurfCoreApi.getCurrentServerDisplayName(),
                    category = SurfCoreApi.getCurrentServerCategory(),
                    startTime = now,
                    endTime = now,
                )
            )

            return
        }

        // Clean up unexpected duplicate sessions, keep only the most recent one
        activeSessions
            .sortedByDescending { it.startTime }
            .drop(1)
            .forEach { duplicate ->
                duplicate.endTime = now
                PlaytimeService.removeCachedSession(duplicate.sessionId)

                plugin.launch {
                    PlaytimeService.saveSession(duplicate)
                }
            }
    }
}
