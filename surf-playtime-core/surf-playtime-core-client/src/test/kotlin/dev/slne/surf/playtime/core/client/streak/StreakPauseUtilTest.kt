package dev.slne.surf.playtime.core.client.streak

import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StreakPauseUtilTest {
    private fun pause(id: Long, start: String, end: String) =
        PlaytimeStreakPause(id, LocalDate.parse(start), LocalDate.parse(end))

    @Test
    fun `reports no bridge without pauses`() {
        assertFalse(
            isGapBridgedByPauses(
                emptyList(),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-05")
            )
        )
    }

    @Test
    fun `bridges a gap covered by a single pause`() {
        assertTrue(
            isGapBridgedByPauses(
                listOf(pause(1, "2026-01-02", "2026-01-04")),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-05")
            )
        )
    }

    @Test
    fun `bridges a gap covered by adjacent pauses`() {
        assertTrue(
            isGapBridgedByPauses(
                listOf(
                    pause(1, "2026-01-02", "2026-01-02"),
                    pause(2, "2026-01-03", "2026-01-04")
                ),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-05")
            )
        )
    }

    @Test
    fun `does not bridge a gap with an uncovered day`() {
        assertFalse(
            isGapBridgedByPauses(
                listOf(
                    pause(1, "2026-01-02", "2026-01-02"),
                    pause(2, "2026-01-04", "2026-01-04")
                ),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-05")
            )
        )
    }

    @Test
    fun `bridges consecutive days without any day in between`() {
        assertTrue(
            isGapBridgedByPauses(
                listOf(pause(1, "2026-02-01", "2026-02-01")),
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-02")
            )
        )
    }
}
