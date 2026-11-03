package dev.slne.surf.playtime.api.common.session

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

/**
 * Selects stored playtime sessions by time window, server, category and player.
 *
 * A session matches if it overlaps the window from [from] (inclusive) to [to] (exclusive) and
 * every given restriction. [servers] and [categories] are compared with [PlaytimeSession.server]
 * and [PlaytimeSession.category]. Within one restriction any value may match; `null` means no
 * restriction and an empty set matches nothing.
 *
 * Times use the same server-local clock as [PlaytimeSession].
 */
@Serializable
data class PlaytimeFilter(
    val from: @Contextual LocalDateTime,
    val to: @Contextual LocalDateTime,
    val servers: Set<String>? = null,
    val categories: Set<String>? = null,
    val players: Set<@Contextual UUID>? = null,
) {
    init {
        require(from.isBefore(to)) { "from ($from) must be before to ($to)" }
    }
}
