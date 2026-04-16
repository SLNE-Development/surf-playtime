package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDateTime

object PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        AfkService.changeState(event.player.uniqueId, false)
        PayCheckService.invalidateCache(event.player.uniqueId)
        plugin.launch {
            val sessions =
                PlaytimeService.activePlaytimeSessions.filter { it.playerUuid == event.player.uniqueId }

            val now = LocalDateTime.now()
            sessions.forEach { session ->
                session.endTime = now
                PlaytimeService.saveSession(session)
                PlaytimeService.removeCachedSession(session.sessionId)
            }
        }
    }
}