package dev.slne.surf.playtime.core.client.session

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class PlayerSessionEpochsTest {
    private val playerUuid: UUID = UUID.randomUUID()

    @Test
    fun `runs a write of the running session`() {
        val epoch = PlayerSessionEpochs.begin(playerUuid)

        var written = false
        PlayerSessionEpochs.ifCurrent(playerUuid, epoch) { written = true }

        assertTrue(written)

        PlayerSessionEpochs.end(playerUuid) {}
    }

    @Test
    fun `skips a write that finishes after the session ended`() {
        val epoch = PlayerSessionEpochs.begin(playerUuid)

        var cleaned = false
        PlayerSessionEpochs.end(playerUuid) { cleaned = true }

        var written = false
        PlayerSessionEpochs.ifCurrent(playerUuid, epoch) { written = true }

        assertTrue(cleaned)
        assertFalse(written)
        assertNull(PlayerSessionEpochs.current(playerUuid))
    }

    @Test
    fun `skips a write of a previous session of the same player`() {
        val firstEpoch = PlayerSessionEpochs.begin(playerUuid)
        PlayerSessionEpochs.end(playerUuid) {}
        val secondEpoch = PlayerSessionEpochs.begin(playerUuid)

        var writtenForFirst = false
        var writtenForSecond = false
        PlayerSessionEpochs.ifCurrent(playerUuid, firstEpoch) { writtenForFirst = true }
        PlayerSessionEpochs.ifCurrent(playerUuid, secondEpoch) { writtenForSecond = true }

        assertFalse(writtenForFirst)
        assertTrue(writtenForSecond)

        PlayerSessionEpochs.end(playerUuid) {}
    }

    @Test
    fun `cleans up even without a tracked session`() {
        var cleaned = false
        PlayerSessionEpochs.end(playerUuid) { cleaned = true }

        assertTrue(cleaned)
        assertNull(PlayerSessionEpochs.current(playerUuid))
    }

    @Test
    fun `runs tracked writes only while the player is tracked`() {
        assertFalse(PlayerSessionEpochs.ifTracked(playerUuid) {})

        val epoch = PlayerSessionEpochs.begin(playerUuid)
        assertEquals(epoch, PlayerSessionEpochs.current(playerUuid))

        var written = false
        assertTrue(PlayerSessionEpochs.ifTracked(playerUuid) { written = true })
        assertTrue(written)

        PlayerSessionEpochs.end(playerUuid) {}
        assertFalse(PlayerSessionEpochs.ifTracked(playerUuid) {})
    }

    @Test
    fun `ignores a write without an epoch`() {
        PlayerSessionEpochs.begin(playerUuid)

        var written = false
        PlayerSessionEpochs.ifCurrent(playerUuid, null) { written = true }

        assertFalse(written)

        PlayerSessionEpochs.end(playerUuid) {}
    }
}
