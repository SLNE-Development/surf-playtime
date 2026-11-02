package dev.slne.surf.playtime.api.common.session

import dev.slne.surf.api.core.serializer.java.datetime.datetime.ldt.SerializableLocalDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

/**
 * Selects stored playtime sessions by time window, server, category and player.
 *
 * A session matches if it overlaps the half-open window [[from], [to]) and matches every given restriction.
 * Within one restriction any value may match. `null` means no restriction, an empty set matches nothing.
 *
 * Times use the same server-local clock as [PlaytimeSession].
 *
 * @property from inclusive start of the window
 * @property to exclusive end of the window, must be after [from]
 * @property servers server names as stored in [PlaytimeSession.server]
 * @property categories server categories as stored in [PlaytimeSession.category]
 * @property players player uuids
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
