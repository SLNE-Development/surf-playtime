package dev.slne.surf.playtime.core.client.session

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * A utility object responsible for managing player session epochs in a thread-safe manner.
 * This allows tracking, validating, and managing the lifecycle of player sessions
 * identified by their unique UUIDs.
 */
object PlayerSessionEpochs {
    private val sequence = AtomicLong()
    private val epochs = ConcurrentHashMap<UUID, Long>()

    /**
     * Starts tracking [playerUuid] and returns the epoch of this server session.
     */
    fun begin(playerUuid: UUID): Long = sequence.incrementAndGet().also { epochs[playerUuid] = it }

    /**
     * The epoch of the current server session of [playerUuid], or `null` if they are not tracked.
     */
    fun current(playerUuid: UUID): Long? = epochs[playerUuid]

    /**
     * Runs [cleanup] and stops tracking [playerUuid], so no [ifCurrent] or [ifTracked] of the
     * ended session can write per-player state again.
     */
    fun end(playerUuid: UUID, cleanup: () -> Unit) {
        epochs.compute(playerUuid) { _, _ ->
            cleanup()
            null
        }
    }

    /**
     * Runs [action] only while [epoch] is still the current server session of [playerUuid].
     */
    fun ifCurrent(playerUuid: UUID, epoch: Long?, action: () -> Unit) {
        if (epoch == null) return

        epochs.computeIfPresent(playerUuid) { _, current ->
            if (current == epoch) {
                action()
            }

            current
        }
    }

    /**
     * Runs [action] only while [playerUuid] is tracked and returns whether it ran.
     */
    fun ifTracked(playerUuid: UUID, action: () -> Unit): Boolean =
        epochs.computeIfPresent(playerUuid) { _, epoch ->
            action()
            epoch
        } != null
}
