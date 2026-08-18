package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.playtime.core.client.afk.AfkTracker
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.time.Duration.Companion.seconds

object PlayerAfkListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!event.hasChangedOrientation()) return
        markActive(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        AfkTracker.track(event.player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        AfkTracker.forget(event.player.uniqueId)
    }

    fun startAfkCheckTask() {
        plugin.scope.runAtFixedRate(1.seconds, taskName = "afk-check") {
            checkAfkStates()
        }
    }

    private fun markActive(player: Player) {
        if (AfkTracker.markActive(player.uniqueId)) {
            applyAfkState(player, false)
        }
    }

    private fun checkAfkStates() {
        val now = System.nanoTime()

        for (uuid in AfkTracker.idlePlayers(now)) {
            if (!AfkTracker.markAfkIfStillIdle(uuid, now)) continue

            val player = Bukkit.getPlayer(uuid)
            if (player == null) {
                AfkTracker.forget(uuid)
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

        AfkTracker.applyState(uuid, isAfk) {
            plugin.launch(plugin.globalRegionDispatcher) {
                AfkStateChangeEvent(uuid, isAfk).callEvent()
            }
        }
    }
}
