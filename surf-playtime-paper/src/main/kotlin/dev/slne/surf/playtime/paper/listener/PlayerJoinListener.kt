package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.playtimePlayerService
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        playtimeService.activePlaytimeSessions.add(
            PlaytimeSession(
                event.player.uniqueId,
                UUID.randomUUID(),
                surfCoreApi.getCurrentServerName(),
                surfCoreApi.getCurrentServerCategory(),
                1L
            )
        )

        plugin.launch {
            playtimePlayerService.savePlayer(
                PlaytimePlayer(
                    event.player.name,
                    event.player.uniqueId
                )
            )
        }
    }
}