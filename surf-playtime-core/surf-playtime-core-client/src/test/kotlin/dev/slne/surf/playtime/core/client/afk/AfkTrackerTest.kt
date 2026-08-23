package dev.slne.surf.playtime.core.client.afk

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.time.Duration.Companion.minutes

class AfkTrackerTest {
    private val afkTimeNanos = 3.minutes.inWholeNanoseconds
    private val playerUuid: UUID = UUID.randomUUID()

    @AfterEach
    fun forget() {
        AfkTracker.forget(playerUuid)
    }

    @Test
    fun `reports a tracked player as idle only after the afk time`() {
        AfkTracker.track(playerUuid)

        val now = System.nanoTime()

        assertFalse(AfkTracker.idlePlayers(now).contains(playerUuid))
        assertTrue(AfkTracker.idlePlayers(now + afkTimeNanos).contains(playerUuid))
    }

    @Test
    fun `marks an idle player afk exactly once`() {
        AfkTracker.track(playerUuid)

        val idleAt = System.nanoTime() + afkTimeNanos

        assertTrue(AfkTracker.markAfkIfStillIdle(playerUuid, idleAt))
        assertFalse(AfkTracker.markAfkIfStillIdle(playerUuid, idleAt))
    }

    @Test
    fun `stops reporting a player that is already afk as idle`() {
        AfkTracker.track(playerUuid)

        val idleAt = System.nanoTime() + afkTimeNanos
        AfkTracker.markAfkIfStillIdle(playerUuid, idleAt)

        assertFalse(AfkTracker.idlePlayers(idleAt).contains(playerUuid))
    }

    @Test
    fun `reports the way back out of the afk state exactly once`() {
        AfkTracker.track(playerUuid)
        AfkTracker.markAfkIfStillIdle(playerUuid, System.nanoTime() + afkTimeNanos)

        assertTrue(AfkTracker.markActive(playerUuid))
        assertFalse(AfkTracker.markActive(playerUuid))
    }

    @Test
    fun `does not mark a player afk that is not idle yet`() {
        AfkTracker.track(playerUuid)

        assertFalse(AfkTracker.markAfkIfStillIdle(playerUuid, System.nanoTime()))
    }

    @Test
    fun `ignores activity of a player that is not tracked`() {
        assertFalse(AfkTracker.markActive(playerUuid))

        // Activity must not start tracking a player again, otherwise an event that races the
        // disconnect would keep the state of a player that already left.
        assertFalse(
            AfkTracker.idlePlayers(System.nanoTime() + afkTimeNanos).contains(playerUuid)
        )
    }

    @Test
    fun `forgets a tracked player`() {
        AfkTracker.track(playerUuid)
        AfkTracker.forget(playerUuid)

        assertFalse(
            AfkTracker.idlePlayers(System.nanoTime() + afkTimeNanos).contains(playerUuid)
        )
        assertFalse(AfkTracker.markAfkIfStillIdle(playerUuid, System.nanoTime() + afkTimeNanos))
    }
}
