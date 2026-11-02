package dev.slne.surf.playtime.database.repository

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import java.time.Duration
import java.time.LocalDateTime

/**
 * Returns the whole seconds of the session from [start] to [end] that lie within the window from
 * [from] (inclusive) to [to] (exclusive).
 */
internal fun secondsWithin(
    start: LocalDateTime,
    end: LocalDateTime,
    from: LocalDateTime,
    to: LocalDateTime
): Long {
    val clippedStart = if (start.isAfter(from)) start else from
    val clippedEnd = if (end.isBefore(to)) end else to

    if (!clippedStart.isBefore(clippedEnd)) {
        return 0
    }

    return Duration.between(clippedStart, clippedEnd).seconds
}

/**
 * Whether one of the restrictions of this filter is an empty set, so no session can match.
 */
internal fun PlaytimeFilter.matchesNothing() =
    servers?.isEmpty() == true || categories?.isEmpty() == true || players?.isEmpty() == true
