package dev.slne.surf.playtime.api.session

import java.time.Duration
import java.time.LocalDateTime
import java.util.*

data class PlaytimeSession(
    val playerUuid: UUID,
    val sessionId: UUID,
    val server: String,
    val category: String,
    val startTime: LocalDateTime,
    var endTime: LocalDateTime
) {
    val durationSeconds get() = Duration.between(startTime, endTime).seconds
}
