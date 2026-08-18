package dev.slne.surf.playtime.core.client.streak

import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import java.time.LocalDate

/**
 * Checks whether every day strictly between [lastLogin] and [today] is covered by a
 * streak pause, so the streak survives e.g. maintenance downtimes.
 */
fun isGapBridgedByPauses(
    pauses: List<PlaytimeStreakPause>,
    lastLogin: LocalDate,
    today: LocalDate
): Boolean {
    if (pauses.isEmpty()) {
        return false
    }

    var day = lastLogin.plusDays(1)
    while (day.isBefore(today)) {
        if (pauses.none { day in it }) {
            return false
        }
        day = day.plusDays(1)
    }

    return true
}
