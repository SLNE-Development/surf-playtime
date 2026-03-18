package dev.slne.surf.playtime.core.service

import dev.slne.surf.playtime.core.config.PlaytimeConfig
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val payCheckService = requiredService<PayCheckService>()

interface PayCheckService {
    fun create(config: PlaytimeConfig)

    fun getCurrentPlaytime(playerUuid: UUID): Long
    suspend fun onIncreasedPlaytime(playerUuid: UUID, amount: Long)

    suspend fun handleUpdate(playerUuid: UUID, newPlaytime: Long)
    suspend fun cachePlaytime(playerUuid: UUID)
    fun invalidateCache(playerUuid: UUID)
}