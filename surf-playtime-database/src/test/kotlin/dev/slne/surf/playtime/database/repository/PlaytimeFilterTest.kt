package dev.slne.surf.playtime.database.repository

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class PlaytimeFilterTest {
    private val from = LocalDateTime.parse("2026-11-02T00:00:00")
    private val to = LocalDateTime.parse("2026-11-03T00:00:00")

    @Test
    fun `a filter without restrictions matches something`() {
        assertFalse(PlaytimeFilter(from, to).matchesNothing())
    }

    @Test
    fun `a filter with non-empty restrictions matches something`() {
        val filter = PlaytimeFilter(
            from,
            to,
            servers = setOf("Event"),
            categories = setOf("event"),
            players = setOf(UUID.randomUUID())
        )

        assertFalse(filter.matchesNothing())
    }

    @Test
    fun `a filter with an empty server set matches nothing`() {
        assertTrue(PlaytimeFilter(from, to, servers = emptySet()).matchesNothing())
    }

    @Test
    fun `a filter with an empty category set matches nothing`() {
        assertTrue(PlaytimeFilter(from, to, categories = emptySet()).matchesNothing())
    }

    @Test
    fun `a filter with an empty player set matches nothing`() {
        assertTrue(PlaytimeFilter(from, to, players = emptySet()).matchesNothing())
    }

    @Test
    fun `rejects a window that does not end after its start`() {
        assertThrows<IllegalArgumentException> { PlaytimeFilter(from, from) }
        assertThrows<IllegalArgumentException> { PlaytimeFilter(to, from) }
    }
}
