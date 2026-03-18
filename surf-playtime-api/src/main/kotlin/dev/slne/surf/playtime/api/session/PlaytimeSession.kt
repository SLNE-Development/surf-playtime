package dev.slne.surf.playtime.api.session

import java.util.*

data class PlaytimeSession(
    val playerUuid: UUID,
    val sessionId: UUID,
    val server: String,
    val category: String,
    var seconds: Long = 0L
) {
    val durationSeconds get() = seconds
}
