package dev.slne.surf.playtime.core.client.command

import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class PlaytimeSummaryTest {
    private val start: LocalDateTime = LocalDateTime.of(2026, 1, 1, 12, 0)

    private fun session(category: String, server: String, seconds: Long) = PlaytimeSession(
        playerUuid = UUID.randomUUID(),
        sessionId = UUID.randomUUID(),
        server = server,
        category = category,
        startTime = start,
        endTime = start.plusSeconds(seconds)
    )

    private val sessions = listOf(
        session("survival", "survival-1", 60),
        session("survival", "survival-1", 30),
        session("survival", "survival-2", 10),
        session("lobby", "lobby-1", 5)
    )

    @Test
    fun `groups playtime by category and server`() {
        assertEquals(
            mapOf(
                "survival" to mapOf("survival-1" to 90L, "survival-2" to 10L),
                "lobby" to mapOf("lobby-1" to 5L)
            ),
            sessions.sumPlaytime()
        )
    }

    @Test
    fun `sums playtime of a single category`() {
        assertEquals(100L, sessions.sumByCategory("survival"))
        assertEquals(5L, sessions.sumByCategory("lobby"))
    }

    @Test
    fun `sums an unknown category as zero`() {
        assertEquals(0L, sessions.sumByCategory("creative"))
    }

    @Test
    fun `summarizes an empty session set as empty`() {
        assertEquals(emptyMap<String, Map<String, Long>>(), emptyList<PlaytimeSession>().sumPlaytime())
    }
}
