package dev.slne.surf.playtime.core.client.session

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.core.client.streak.isGapBridgedByPauses
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import net.kyori.adventure.audience.Audience
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Starts tracking the playtime of a player that just joined this server.
 */
fun startPlaytimeSession(playerUuid: UUID) {
    PlayerSessionEpochs.begin(playerUuid)
    AfkService.changeState(playerUuid, false)

    PlaytimeService.cacheSession(
        PlaytimeSession(
            playerUuid,
            UUID.randomUUID(),
            SurfCoreApi.getCurrentServerDisplayName(),
            SurfCoreApi.getCurrentServerCategory(),
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    )
}

/**
 * Loads the paycheck playtime of a joining player and advances their login streak.
 */
suspend fun handlePlaytimeJoin(playerUuid: UUID, audience: Audience) {
    val epoch = PlayerSessionEpochs.current(playerUuid)

    PayCheckService.cachePlaytime(playerUuid)

    val streak = PlaytimeStreakService.loadPlaytimeStreak(playerUuid)
    val today = LocalDate.now()

    if (streak == null) {
        val calculated = PlaytimeStreakService.calculatePlaytimeStreak(playerUuid) + 1

        val cached = PlaytimeStreak.SimpleStreak(
            currentLoginStreak = calculated,
            longestLoginStreak = calculated
        )

        cacheStreakIfStillOnServer(playerUuid, epoch, cached)
        PlaytimeStreakService.savePlaytimeStreak(playerUuid, calculated, today)

        audience.sendText {
            appendInfoPrefix()
            info("Deine Login-Streak wurde neu erstellt und auf ")
            variableValue("$calculated Tage")
            info(" berechnet.")
        }

        return
    }

    val lastLogin = streak.lastLoginDate

    val newStreak = when {
        lastLogin == null -> 1
        lastLogin.isEqual(today) -> streak.currentLoginStreak
        lastLogin.plusDays(1).isEqual(today) -> streak.currentLoginStreak + 1
        isGapBridgedByPauses(
            PlaytimeStreakService.loadStreakPauses(),
            lastLogin,
            today
        ) -> streak.currentLoginStreak + 1

        else -> 1
    }

    val newLongest = maxOf(streak.longestLoginStreak, newStreak)

    val cached = PlaytimeStreak.SimpleStreak(
        currentLoginStreak = newStreak,
        longestLoginStreak = newLongest
    )

    cacheStreakIfStillOnServer(playerUuid, epoch, cached)
    PlaytimeStreakService.savePlaytimeStreak(playerUuid, newStreak, today)
}

/**
 * Drops the per-player caches of a player that left this server.
 */
fun invalidatePlaytimeCaches(playerUuid: UUID) {
    PlayerSessionEpochs.end(playerUuid) {
        AfkService.changeState(playerUuid, false)
        PayCheckService.invalidateCache(playerUuid)
        PlaytimeStreakService.invalidateCache(playerUuid)
    }
}

/**
 * Closes and persists every active session of a player that left this server.
 */
suspend fun endPlaytimeSessions(playerUuid: UUID) {
    val sessions = PlaytimeService.activeSessionsOf(playerUuid)

    val now = LocalDateTime.now()
    sessions.forEach { session ->
        session.endTime = now
        PlaytimeService.saveSession(session)
        PlaytimeService.removeCachedSession(session.sessionId)
    }
}

private fun cacheStreakIfStillOnServer(
    playerUuid: UUID,
    epoch: Long?,
    streak: PlaytimeStreak.SimpleStreak
) {
    PlayerSessionEpochs.ifCurrent(playerUuid, epoch) {
        PlaytimeStreakService.cacheStreak(playerUuid, streak)
    }
}
