package dev.slne.surf.playtime.paper.api

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.SurfPlaytimeApi
import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.afkService
import dev.slne.surf.playtime.core.service.playtimePlayerService
import dev.slne.surf.playtime.core.service.playtimeService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfPlaytimeApi::class)
class SurfPlaytimeApiImpl : SurfPlaytimeApi, Services.Fallback {
    override fun getCurrentPlaytimeSession(playerUuid: UUID): PlaytimeSession? =
        playtimeService.activePlaytimeSessions.find { it.playerUuid == playerUuid }

    override fun isPlayerAfk(playerUuid: UUID): Boolean = afkService.isAfk(playerUuid)

    override suspend fun getPlaytimeByServer(
        playerUuid: UUID,
        server: String
    ): Long {
        val stored =
            playtimeService.loadSessionsByServer(playerUuid, server)
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
            playtimeService.loadSessionsByCategory(playerUuid, category)
                .sumOf { it.durationSeconds }

        val current =
            getCurrentPlaytimeSession(playerUuid)
                ?.takeIf { it.category == category }
                ?.durationSeconds ?: 0

        return stored + current
    }

    override suspend fun getTotalPlaytime(playerUuid: UUID) =
        playtimeService.getAndLoadSessions(playerUuid).sumOf { it.durationSeconds }

    override suspend fun getAllPlaytimeSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        playtimeService.getAndLoadSessions(playerUuid)

    override fun getPlaytimePlayer(name: String): PlaytimePlayer? =
        playtimePlayerService.getPlayer(name)

    override fun getPlaytimePlayer(uuid: UUID): PlaytimePlayer? =
        playtimePlayerService.getPlayer(uuid)
}