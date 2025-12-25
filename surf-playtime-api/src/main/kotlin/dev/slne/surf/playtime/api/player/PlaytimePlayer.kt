package dev.slne.surf.playtime.api.player

import dev.slne.surf.playtime.api.session.PlaytimeSession
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

data class PlaytimePlayer(
    val name: String,
    val uuid: UUID,
    val playtimeSessions: ObjectSet<PlaytimeSession>
)