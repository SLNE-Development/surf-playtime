package dev.slne.surf.playtime.core.client.afk

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.emptyObjectSet
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.client.platform.PlaytimePlatform
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.minutes

/**
 * Tracks how long each player has been idle and turns that into afk state changes.
 */
object AfkTracker {
    private val afkTimeNanos = 3.minutes.inWholeNanoseconds

    private class AfkState(@Volatile var lastActivityNanos: Long) {
        val afk = AtomicBoolean(false)
    }

    private val states = ConcurrentHashMap<UUID, AfkState>()

    /**
     * Starts tracking [playerUuid] as active.
     */
    fun track(playerUuid: UUID) {
        states[playerUuid] = AfkState(System.nanoTime())

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
        val state = states[playerUuid] ?: return false

        state.lastActivityNanos = System.nanoTime()

        return state.afk.get() && state.afk.compareAndSet(true, false)
    }

    /**
     * Returns the players that have been idle long enough to be considered afk at [now].
     */
    @Suppress("JavaMapForEach")
    fun idlePlayers(now: Long): Set<UUID> {
        var idle: ObjectOpenHashSet<UUID>? = null

        states.forEach { uuid, state ->
            if (!state.afk.get() && now - state.lastActivityNanos >= afkTimeNanos) {
                (idle ?: ObjectOpenHashSet<UUID>().also { idle = it }).add(uuid)
            }
        }

        return idle ?: emptyObjectSet()
    }

    /**
     * Marks [playerUuid] as afk unless they became active again since [now] was taken, and
     * returns whether the state actually changed.
     */
    fun markAfkIfStillIdle(playerUuid: UUID, now: Long): Boolean {
        val state = states[playerUuid] ?: return false

        // Recheck — player may have moved by now
        if (now - state.lastActivityNanos < afkTimeNanos) return false
        if (!state.afk.compareAndSet(false, true)) return false

        // Activity may have been recorded between the recheck and the flag flip. Undo the
        // transition instead of sending a player that just moved into afk.
        if (now - state.lastActivityNanos < afkTimeNanos) {
            state.afk.compareAndSet(true, false)
            return false
        }

        return true
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
        val state = states[playerUuid] ?: return false
        if (state.afk.get() != isAfk) return false

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
        val activeSessions = PlaytimeService.activeSessionsOf(playerUuid)

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
