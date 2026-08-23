package dev.slne.surf.playtime.core.client.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import dev.slne.surf.playtime.core.common.rabbit.packet.request.CalculatePlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.CreatePlaytimeStreakPauseRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.DeletePlaytimeStreakPauseRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadPlaytimeStreakPausesRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadPlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.RecalculateAllPlaytimeStreaksRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.RecalculatePlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.SavePlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import net.kyori.adventure.util.Services
import java.time.LocalDate
import java.util.*

@AutoService(PlaytimeStreakService::class)
class PlaytimeStreakServiceImpl : PlaytimeStreakService, Services.Fallback {
    private val streakCache = Caffeine.newBuilder()
        .maximumSize(MAX_CACHED_STREAKS)
        .build<UUID, PlaytimeStreak.SimpleStreak>()

    override suspend fun loadPlaytimeStreak(playerUuid: UUID): PlaytimeStreak? =
        ClientPlaytimeInstance.rabbitApi.sendRequest(LoadPlaytimeStreakRequestPacket(playerUuid)).streak

    override suspend fun savePlaytimeStreak(
        playerUuid: UUID,
        streak: Int,
        localDate: LocalDate
    ): Boolean = ClientPlaytimeInstance.rabbitApi.sendRequest(
        SavePlaytimeStreakRequestPacket(
            playerUuid,
            streak,
            localDate
        )
    ).value

    override fun cacheStreak(playerUuid: UUID, streak: PlaytimeStreak.SimpleStreak) {
        streakCache.put(playerUuid, streak)
    }

    override fun getStreak(playerUuid: UUID): PlaytimeStreak.SimpleStreak? =
        streakCache.getIfPresent(playerUuid)

    override fun invalidateCache(playerUuid: UUID) {
        streakCache.invalidate(playerUuid)
    }

    override suspend fun calculatePlaytimeStreak(playerUuid: UUID): Int =
        ClientPlaytimeInstance.rabbitApi.sendRequest(CalculatePlaytimeStreakRequestPacket(playerUuid)).value

    override suspend fun recalculatePlaytimeStreak(playerUuid: UUID): PlaytimeStreak? =
        ClientPlaytimeInstance.rabbitApi.sendRequest(RecalculatePlaytimeStreakRequestPacket(playerUuid)).streak

    override suspend fun recalculateAllPlaytimeStreaks(): Int =
        ClientPlaytimeInstance.rabbitApi.sendRequest(RecalculateAllPlaytimeStreaksRequestPacket()).value

    override suspend fun loadStreakPauses(): List<PlaytimeStreakPause> =
        ClientPlaytimeInstance.rabbitApi.sendRequest(LoadPlaytimeStreakPausesRequestPacket()).pauses

    override suspend fun createStreakPause(
        startDate: LocalDate,
        endDate: LocalDate
    ): PlaytimeStreakPause = ClientPlaytimeInstance.rabbitApi.sendRequest(
        CreatePlaytimeStreakPauseRequestPacket(startDate, endDate)
    ).pause

    override suspend fun deleteStreakPause(pauseId: Long): Boolean =
        ClientPlaytimeInstance.rabbitApi.sendRequest(DeletePlaytimeStreakPauseRequestPacket(pauseId)).value

    companion object {
        private const val MAX_CACHED_STREAKS = 16_384L
    }
}
