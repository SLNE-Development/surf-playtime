package dev.slne.surf.playtime.api

import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val surfPlaytimeApi = requiredService<SurfPlaytimeApi>()

interface SurfPlaytimeApi {
    fun getCurrentPlaytimeSession(playerUuid: UUID): PlaytimeSession?
    fun isPlayerAfk(playerUuid: UUID): Boolean

    suspend fun getPlaytimeByServer(playerUuid: UUID, server: String): Long
    suspend fun getPlaytimeByCategory(playerUuid: UUID, category: String): Long
    suspend fun getTotalPlaytime(playerUuid: UUID): Long?

    suspend fun getAllPlaytimeSessions(playerUuid: UUID): ObjectSet<PlaytimeSession>

    fun getPlaytimePlayer(name: String): PlaytimePlayer?
    fun getPlaytimePlayer(uuid: UUID): PlaytimePlayer?
}