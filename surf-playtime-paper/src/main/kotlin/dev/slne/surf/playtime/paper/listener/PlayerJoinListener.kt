package dev.slne.surf.playtime.paper.listener

import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.playtimeService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.LocalDateTime
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
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )
    }
}