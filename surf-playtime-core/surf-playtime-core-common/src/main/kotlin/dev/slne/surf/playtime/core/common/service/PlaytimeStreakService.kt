package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import java.time.LocalDate
import java.util.*

private val service = requiredService<PlaytimeStreakService>()

interface PlaytimeStreakService {
    suspend fun loadPlaytimeStreak(playerUuid: UUID): PlaytimeStreak?
    suspend fun savePlaytimeStreak(playerUuid: UUID, streak: Int, localDate: LocalDate): Boolean
    suspend fun calculatePlaytimeStreak(playerUuid: UUID): Int


    fun cacheStreak(playerUuid: UUID, streak: PlaytimeStreak.SimpleStreak)
    fun getStreak(playerUuid: UUID): PlaytimeStreak.SimpleStreak?
    fun invalidateCache(playerUuid: UUID)

    companion object : PlaytimeStreakService by service
}