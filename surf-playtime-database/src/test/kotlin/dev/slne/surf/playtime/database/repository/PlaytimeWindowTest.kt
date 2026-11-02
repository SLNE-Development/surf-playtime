package dev.slne.surf.playtime.database.repository

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class PlaytimeWindowTest {
    private val from = LocalDateTime.parse("2026-11-02T00:00:00")
    private val to = LocalDateTime.parse("2026-11-03T00:00:00")

    private fun time(value: String) = LocalDateTime.parse(value)

    @Test
    fun `counts a session inside the window completely`() {
        assertEquals(
            3600,
            secondsWithin(time("2026-11-02T18:00:00"), time("2026-11-02T19:00:00"), from, to)
        )
    }

    @Test
    fun `counts only the part after the window start`() {
        assertEquals(
            1800,
            secondsWithin(time("2026-11-01T23:00:00"), time("2026-11-02T00:30:00"), from, to)
        )
    }

    @Test
    fun `counts only the part before the window end`() {
        assertEquals(
            900,
            secondsWithin(time("2026-11-02T23:45:00"), time("2026-11-03T01:00:00"), from, to)
        )
    }

    @Test
    fun `counts the window length for a session spanning the whole window`() {
        assertEquals(
            86400,
            secondsWithin(time("2026-11-01T12:00:00"), time("2026-11-03T12:00:00"), from, to)
        )
    }

    @Test
    fun `ignores a session ending exactly at the window start`() {
        assertEquals(0, secondsWithin(time("2026-11-01T23:00:00"), from, from, to))
    }

    @Test
    fun `ignores a session starting exactly at the window end`() {
        assertEquals(0, secondsWithin(to, time("2026-11-03T01:00:00"), from, to))
    }

    @Test
    fun `ignores a session outside the window`() {
        assertEquals(
            0,
            secondsWithin(time("2026-11-04T10:00:00"), time("2026-11-04T11:00:00"), from, to)
        )
    }

    @Test
    fun `truncates partial seconds`() {
        assertEquals(
            10,
            secondsWithin(time("2026-11-02T10:00:00"), time("2026-11-02T10:00:10.900"), from, to)
        )
    }

    @Test
    fun `adjacent windows do not count the same second twice`() {
        val start = time("2026-11-02T23:30:00")
        val end = time("2026-11-03T00:30:00")
        val nextDay = PlaytimeFilter(to, to.plusDays(1))

        assertEquals(
            3600,
            secondsWithin(start, end, from, to) + secondsWithin(start, end, nextDay.from, nextDay.to)
        )
    }

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
