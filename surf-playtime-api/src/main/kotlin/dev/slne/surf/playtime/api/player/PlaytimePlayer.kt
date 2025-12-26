package dev.slne.surf.playtime.api.player

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlaytimePlayer(
    val name: String,
    val uuid: @Contextual UUID
)