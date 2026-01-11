package dev.slne.surf.playtime.api.session

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Serializable
data class PlaytimeSession(
    val playerUuid: @Contextual UUID,
    val sessionId: @Contextual UUID,
    val server: String,
    val category: String,
    val startTime: @Contextual LocalDateTime,
    var endTime: @Contextual LocalDateTime
) {
    val durationSeconds get() = Duration.between(startTime, endTime).seconds
}
