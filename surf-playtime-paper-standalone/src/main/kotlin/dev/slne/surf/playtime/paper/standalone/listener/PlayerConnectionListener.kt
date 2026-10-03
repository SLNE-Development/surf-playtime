package dev.slne.surf.playtime.paper.standalone.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.paper.standalone.plugin
import dev.slne.surf.playtime.paper.standalone.service.PlaytimeSessionService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        PlaytimeSessionService.startSession(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val playerUuid = event.player.uniqueId

        plugin.launch {
            PlaytimeSessionService.endSession(playerUuid)
        }
    }
}
