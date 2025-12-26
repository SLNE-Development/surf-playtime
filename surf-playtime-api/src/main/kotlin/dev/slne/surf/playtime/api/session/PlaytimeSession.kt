package dev.slne.surf.playtime.api.session

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlaytimeSession(
    val playerUuid: @Contextual UUID,
    val sessionId: @Contextual UUID,
    val server: String,
    val category: String,
    var durationSeconds: Long,
) {
    fun increasePlaytime(seconds: Long = 1L) {
        durationSeconds += seconds
    }
}
