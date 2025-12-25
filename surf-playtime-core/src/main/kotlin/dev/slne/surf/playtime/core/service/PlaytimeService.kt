package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet

val playtimeService = requiredService<PlaytimeService>()

interface PlaytimeService {
    val activePlaytimeSessions: ObjectSet<PlaytimeSession>
}