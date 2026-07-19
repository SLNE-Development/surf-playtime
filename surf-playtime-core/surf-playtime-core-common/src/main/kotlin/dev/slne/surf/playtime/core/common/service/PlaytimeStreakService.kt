package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import java.time.LocalDate
import java.util.*

private val service = requiredService<PlaytimeStreakService>()

interface PlaytimeStreakService {
    suspend fun loadPlaytimeStreak(playerUuid: UUID): PlaytimeStreak?
    suspend fun savePlaytimeStreak(playerUuid: UUID, streak: Int, localDate: LocalDate): Boolean
    suspend fun calculatePlaytimeStreak(playerUuid: UUID): Int
    suspend fun recalculatePlaytimeStreak(playerUuid: UUID): PlaytimeStreak?
    suspend fun recalculateAllPlaytimeStreaks(): Int

    suspend fun loadStreakPauses(): List<PlaytimeStreakPause>
    suspend fun createStreakPause(startDate: LocalDate, endDate: LocalDate): PlaytimeStreakPause
    suspend fun deleteStreakPause(pauseId: Long): Boolean

    suspend fun loadOrCalculateStreak(playerUuid: UUID): PlaytimeStreak.SimpleStreak {
        val cached = getStreak(playerUuid)
        if (cached != null) {
            return cached
        }

        val loaded = loadPlaytimeStreak(playerUuid)
        if (loaded != null) {
            val simple = PlaytimeStreak.SimpleStreak(
                currentLoginStreak = loaded.currentLoginStreak,
                longestLoginStreak = loaded.longestLoginStreak
            )
            cacheStreak(playerUuid, simple)
            return simple
        }

        val calculated = calculatePlaytimeStreak(playerUuid)
        val newStreak = PlaytimeStreak.SimpleStreak(
            currentLoginStreak = calculated,
            longestLoginStreak = calculated
        )
        cacheStreak(playerUuid, newStreak)
        return newStreak
    }


    fun cacheStreak(playerUuid: UUID, streak: PlaytimeStreak.SimpleStreak)
    fun getStreak(playerUuid: UUID): PlaytimeStreak.SimpleStreak?
    fun invalidateCache(playerUuid: UUID)

    companion object : PlaytimeStreakService by service
}