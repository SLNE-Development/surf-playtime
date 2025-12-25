package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.PlaytimeService
import dev.slne.surf.playtime.core.service.playtimePlayerService
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(PlaytimeService::class)
class PlaytimeServiceImpl : PlaytimeService, Services.Fallback {
    override val activePlaytimeSessions = mutableObjectSetOf<PlaytimeSession>()

}