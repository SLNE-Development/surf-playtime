package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.payCheckService
import dev.slne.surf.playtime.core.common.service.playtimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.LocalDateTime
import java.util.*

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        AfkService.changeState(event.player.uniqueId, false)

        playtimeService.cacheSession(
            PlaytimeSession(
                event.player.uniqueId,
                UUID.randomUUID(),
                SurfCoreApi.getCurrentServerDisplayName(),
                SurfCoreApi.getCurrentServerCategory(),
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )

        plugin.launch {
            payCheckService.cachePlaytime(event.player.uniqueId)
        }
    }
}