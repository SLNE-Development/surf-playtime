package dev.slne.surf.playtime.api.common.session

import dev.slne.surf.api.core.serializer.java.datetime.datetime.ldt.SerializableLocalDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable
import java.time.Duration

@Serializable
data class PlaytimeSession(
    val playerUuid: SerializableUUID,
    val sessionId: SerializableUUID,
    val server: String,
    val category: String,
    val startTime: SerializableLocalDateTime,
    var endTime: SerializableLocalDateTime
) {
    val durationSeconds get() = Duration.between(startTime, endTime).seconds
}
