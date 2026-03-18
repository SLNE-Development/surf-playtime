package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.service.afkService
import dev.slne.surf.playtime.core.service.payCheckService
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        afkService.changeState(event.player.uniqueId, false)
        payCheckService.invalidateCache(event.player.uniqueId)
        plugin.launch {
            val session =
                playtimeService.activePlaytimeSessions.find { it.playerUuid == event.player.uniqueId }
                    ?: return@launch

            playtimeService.saveSession(session)
            playtimeService.activePlaytimeSessions.removeIf { it.playerUuid == event.player.uniqueId }
        }
    }
}
