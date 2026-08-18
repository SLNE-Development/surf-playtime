package dev.slne.surf.playtime.core.client.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PlaytimeFormatTest {
    @Test
    fun `formats seconds only below a minute`() {
        assertEquals("0s", 0L.formatSeconds())
        assertEquals("59s", 59L.formatSeconds())
    }

    @Test
    fun `formats minutes with padded seconds`() {
        assertEquals("1m 00s", 60L.formatSeconds())
        assertEquals("59m 59s", 3599L.formatSeconds())
    }

    @Test
    fun `formats hours with padded minutes and seconds`() {
        assertEquals("1h 00m 00s", 3600L.formatSeconds())
        assertEquals("2h 03m 04s", (2 * 3600 + 3 * 60 + 4).toLong().formatSeconds())
    }

    @Test
    fun `parses iso dates`() {
        assertEquals(LocalDate.of(2026, 12, 31), parseDate("2026-12-31"))
    }

    @Test
    fun `parses german dates`() {
        assertEquals(LocalDate.of(2026, 12, 31), parseDate("31.12.2026"))
    }

    @Test
    fun `rejects unknown date formats`() {
        assertNull(parseDate("31/12/2026"))
        assertNull(parseDate("gestern"))
        assertNull(parseDate(""))
    }
}
