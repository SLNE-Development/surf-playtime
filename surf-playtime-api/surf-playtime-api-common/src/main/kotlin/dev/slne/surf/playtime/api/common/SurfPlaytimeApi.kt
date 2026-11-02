package dev.slne.surf.playtime.api.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import it.unimi.dsi.fastutil.objects.Object2LongMap
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

    /**
     * Sums the stored playtime per player of all sessions matching [filter].
     *
     * Only the part of a session inside the window of [filter] is counted. Sessions that are still running are
     * included as far as they have been saved, which happens periodically and when a player leaves a server.
     *
     * @return playtime in seconds by player uuid, without players that have no matching playtime
     */
    suspend fun getPlaytimePerPlayer(filter: PlaytimeFilter): Object2LongMap<UUID>
}