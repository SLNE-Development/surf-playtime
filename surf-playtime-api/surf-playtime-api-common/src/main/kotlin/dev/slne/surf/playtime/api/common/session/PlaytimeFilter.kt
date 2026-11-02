package dev.slne.surf.playtime.api.common.session

import dev.slne.surf.api.core.serializer.java.datetime.datetime.ldt.SerializableLocalDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

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
    val from: SerializableLocalDateTime,
    val to: SerializableLocalDateTime,
    val servers: Set<String>? = null,
    val categories: Set<String>? = null,
    val players: Set<SerializableUUID>? = null,
) {
    init {
        require(from.isBefore(to)) { "from ($from) must be before to ($to)" }
    }
}
