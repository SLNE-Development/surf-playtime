package dev.slne.surf.playtime.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import dev.slne.surf.playtime.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        AfkService.changeState(event.player.uniqueId, false)

        PlaytimeService.cacheSession(
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
            val uuid = event.player.uniqueId
            PayCheckService.cachePlaytime(uuid)

            val streak = PlaytimeStreakService.loadPlaytimeStreak(uuid)
            val today = LocalDate.now()

            if (streak == null) {
                val calculated = PlaytimeStreakService.calculatePlaytimeStreak(uuid) + 1

                val cached = PlaytimeStreak.SimpleStreak(
                    currentLoginStreak = calculated,
                    longestLoginStreak = calculated
                )

                PlaytimeStreakService.cacheStreak(uuid, cached)
                PlaytimeStreakService.savePlaytimeStreak(uuid, calculated, today)

                event.player.sendText {
                    appendInfoPrefix()
                    info("Deine Login-Streak wurde neu erstellt und auf ")
                    variableValue("$calculated Tage")
                    info(" berechnet.")
                }

                return@launch
            }

            val lastLogin = streak.lastLoginDate

            val newStreak = when {
                lastLogin == null -> 1
                lastLogin.isEqual(today) -> streak.currentLoginStreak
                lastLogin.plusDays(1).isEqual(today) -> streak.currentLoginStreak + 1
                else -> 1
            }

            val newLongest = maxOf(streak.longestLoginStreak, newStreak)

            val cached = PlaytimeStreak.SimpleStreak(
                currentLoginStreak = newStreak,
                longestLoginStreak = newLongest
            )

            PlaytimeStreakService.cacheStreak(uuid, cached)
            PlaytimeStreakService.savePlaytimeStreak(uuid, newStreak, today)
        }
    }
}