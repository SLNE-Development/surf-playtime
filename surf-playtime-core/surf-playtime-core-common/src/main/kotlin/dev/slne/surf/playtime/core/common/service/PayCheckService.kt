package dev.slne.surf.playtime.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.playtime.core.common.config.PlaytimeConfig
import java.util.*

private val service = requiredService<PayCheckService>()

interface PayCheckService {
    fun create(config: PlaytimeConfig)

    fun getCurrentPlaytime(playerUuid: UUID): Long
    suspend fun increasePlaytime(playerUuid: UUID, amount: Long)

    suspend fun handleUpdate(playerUuid: UUID, newPlaytime: Long)
    suspend fun cachePlaytime(playerUuid: UUID)
    fun invalidateCache(playerUuid: UUID)

    companion object : PayCheckService by service
}