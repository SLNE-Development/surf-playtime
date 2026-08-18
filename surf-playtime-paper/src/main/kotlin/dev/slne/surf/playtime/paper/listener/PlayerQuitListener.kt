package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.client.session.endPlaytimeSessions
import dev.slne.surf.playtime.core.client.session.invalidatePlaytimeCaches
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        invalidatePlaytimeCaches(event.player.uniqueId)

        plugin.launch {
            endPlaytimeSessions(event.player.uniqueId)
        }
    }
}
