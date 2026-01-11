package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDateTime

object PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        plugin.launch {
            val session =
                playtimeService.activePlaytimeSessions.find { it.playerUuid == event.player.uniqueId }
                    ?: return@launch

            playtimeService.saveSession(session.apply {
                endTime = LocalDateTime.now()
            })
            playtimeService.activePlaytimeSessions.removeIf { it.playerUuid == event.player.uniqueId }
        }
    }
}