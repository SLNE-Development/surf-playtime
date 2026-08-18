package dev.slne.surf.playtime.core.client.afk

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.client.platform.PlaytimePlatform
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes

/**
 * Tracks how long each player has been idle and turns that into afk state changes.
 */
object AfkTracker {
    private val afkTimeNanos = 3.minutes.inWholeNanoseconds

    private data class AfkState(
        val lastActivityNanos: Long,
        val isAfk: Boolean,
    )

    private val states = ConcurrentHashMap<UUID, AfkState>()

    /**
     * Starts tracking [playerUuid] as active.
     */
    fun track(playerUuid: UUID) {
        states[playerUuid] = AfkState(
            lastActivityNanos = System.nanoTime(),
            isAfk = false,
        )

        AfkService.changeState(playerUuid, false)
    }

    /**
     * Stops tracking [playerUuid].
     */
    fun forget(playerUuid: UUID) {
        states.remove(playerUuid)
    }

    /**
     * Records activity of [playerUuid] and returns whether they were afk until now.
     */
    fun markActive(playerUuid: UUID): Boolean {
        val now = System.nanoTime()
        var becameActive = false

        states.compute(playerUuid) { _, previous ->
            becameActive = previous?.isAfk == true

            AfkState(
                lastActivityNanos = now,
                isAfk = false,
            )
        }

        return becameActive
    }

    /**
     * Returns the players that have been idle long enough to be considered afk at [now].
     */
    fun idlePlayers(now: Long): Set<UUID> {
        val result = ObjectOpenHashSet<UUID>()

        for ((uuid, state) in states) {
            if (!state.isAfk && now - state.lastActivityNanos >= afkTimeNanos) {
                result.add(uuid)
            }
        }

        return result
    }

    /**
     * Marks [playerUuid] as afk unless they became active again since [now] was taken, and
     * returns whether the state actually changed.
     */
    fun markAfkIfStillIdle(playerUuid: UUID, now: Long): Boolean {
        var becameAfk = false

        states.computeIfPresent(playerUuid) { _, current ->
            // Recheck — player may have moved by now
            if (current.isAfk || now - current.lastActivityNanos < afkTimeNanos) {
                current
            } else {
                becameAfk = true
                current.copy(isAfk = true)
            }
        }

        return becameAfk
    }

    /**
     * Publishes the tracked afk state of [playerUuid], updating their playtime session and
     * telling them about the change.
     *
     * [onChanged] runs after the session has been updated and before the player is notified.
     *
     * Returns `false` when the tracked state no longer matches [isAfk], which happens when the
     * state changed again since the change was scheduled.
     */
    fun applyState(
        playerUuid: UUID,
        isAfk: Boolean,
        onChanged: () -> Unit = {},
    ): Boolean {
        // Check that the state is still the same, as it may have changed since the task was scheduled
        if (states[playerUuid]?.isAfk != isAfk) return false

        AfkService.changeState(playerUuid, isAfk)
        updatePlaytimeSession(playerUuid, isAfk)

        onChanged()

        PlaytimePlatform.onlinePlayer(playerUuid)?.sendText {
            appendInfoPrefix()
            info("Du bist nun ")

            if (isAfk) {
                info("AFK.")
            } else {
                info("nicht mehr AFK.")
            }
        }

        return true
    }

    private fun updatePlaytimeSession(playerUuid: UUID, isAfk: Boolean) {
        val now = LocalDateTime.now()
        val activeSessions = PlaytimeService.activePlaytimeSessions
            .filter { it.playerUuid == playerUuid }

        if (isAfk) {
            activeSessions.forEach { session ->
                session.endTime = now
                PlaytimeService.removeCachedSession(session.sessionId)

                PlaytimePlatform.launchAsync {
                    PlaytimeService.saveSession(session)
                }
            }

            return
        }

        if (activeSessions.isEmpty()) {
            PlaytimeService.cacheSession(
                PlaytimeSession(
                    playerUuid = playerUuid,
                    sessionId = UUID.randomUUID(),
                    server = SurfCoreApi.getCurrentServerDisplayName(),
                    category = SurfCoreApi.getCurrentServerCategory(),
                    startTime = now,
                    endTime = now,
                )
            )

            return
        }

        // Clean up unexpected duplicate sessions, keep only the most recent one
        activeSessions
            .sortedByDescending { it.startTime }
            .drop(1)
            .forEach { duplicate ->
                duplicate.endTime = now
                PlaytimeService.removeCachedSession(duplicate.sessionId)

                PlaytimePlatform.launchAsync {
                    PlaytimeService.saveSession(duplicate)
                }
            }
    }
}
