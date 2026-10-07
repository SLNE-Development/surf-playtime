package dev.slne.surf.playtime.paper.standalone.api

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.common.SurfPlaytimeApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.paper.standalone.afk.AfkTracker
import dev.slne.surf.playtime.paper.standalone.service.PlaytimeSessionService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfPlaytimeApi::class)
class SurfPlaytimeApiImpl : SurfPlaytimeApi, Services.Fallback {
    override fun getCurrentPlaytimeSession(playerUuid: UUID): PlaytimeSession? =
        PlaytimeSessionService.activeSessionOf(playerUuid)

    override fun isPlayerAfk(playerUuid: UUID): Boolean = AfkTracker.isAfk(playerUuid)

    override suspend fun getPlaytimeByServer(playerUuid: UUID, server: String): Long =
        PlaytimeSessionService.getAndLoadSessionsByServer(playerUuid, server)
            .sumOf { it.durationSeconds }

    override suspend fun getPlaytimeByCategory(playerUuid: UUID, category: String): Long =
        PlaytimeSessionService.getAndLoadSessionsByCategory(playerUuid, category)
            .sumOf { it.durationSeconds }

    override suspend fun getTotalPlaytime(playerUuid: UUID) =
        PlaytimeSessionService.getAndLoadSessions(playerUuid).sumOf { it.durationSeconds }

    override suspend fun getAllPlaytimeSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        PlaytimeSessionService.getAndLoadSessions(playerUuid)
}
