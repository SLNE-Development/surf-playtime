package dev.slne.surf.playtime.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.common.SurfPlaytimeApi
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.common.service.AfkService
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfPlaytimeApi::class)
class SurfPlaytimeApiImpl : SurfPlaytimeApi, Services.Fallback {
    override fun getCurrentPlaytimeSession(playerUuid: UUID): PlaytimeSession? =
        PlaytimeService.activePlaytimeSessions.find { it.playerUuid == playerUuid }

    override fun isPlayerAfk(playerUuid: UUID): Boolean = AfkService.isAfk(playerUuid)

    override suspend fun getPlaytimeByServer(
        playerUuid: UUID,
        server: String
    ): Long {
        val stored =
            PlaytimeService.loadSessionsByServer(playerUuid, server)
                .sumOf { it.durationSeconds }

        val current =
            getCurrentPlaytimeSession(playerUuid)
                ?.takeIf { it.server == server }
                ?.durationSeconds ?: 0

        return stored + current
    }


    override suspend fun getPlaytimeByCategory(
        playerUuid: UUID,
        category: String
    ): Long {
        val stored =
            PlaytimeService.loadSessionsByCategory(playerUuid, category)
                .sumOf { it.durationSeconds }

        val current =
            getCurrentPlaytimeSession(playerUuid)
                ?.takeIf { it.category == category }
                ?.durationSeconds ?: 0

        return stored + current
    }

    override suspend fun getTotalPlaytime(playerUuid: UUID) =
        PlaytimeService.getAndLoadSessions(playerUuid).sumOf { it.durationSeconds }

    override suspend fun getAllPlaytimeSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        PlaytimeService.getAndLoadSessions(playerUuid)
}