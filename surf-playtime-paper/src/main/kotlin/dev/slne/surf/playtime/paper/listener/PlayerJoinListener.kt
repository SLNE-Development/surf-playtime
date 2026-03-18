package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.afkService
import dev.slne.surf.playtime.core.service.payCheckService
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.LocalDateTime
import java.util.*

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        afkService.changeState(event.player.uniqueId, false)

        playtimeService.cacheSession(
            PlaytimeSession(
                event.player.uniqueId,
                UUID.randomUUID(),
                surfCoreApi.getCurrentServerDisplayName(),
                surfCoreApi.getCurrentServerCategory(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )

        plugin.launch {
            payCheckService.cachePlaytime(event.player.uniqueId)
        }
    }
}