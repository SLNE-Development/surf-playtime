package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.client.session.handlePlaytimeJoin
import dev.slne.surf.playtime.core.client.session.startPlaytimeSession
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        startPlaytimeSession(event.player.uniqueId)

        plugin.launch {
            handlePlaytimeJoin(event.player.uniqueId, event.player)
        }
    }
}
