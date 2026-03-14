package dev.slne.surf.playtime.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.playtime.core.bridge.transactionBridge
import dev.slne.surf.playtime.core.config.PlaytimeConfig
import dev.slne.surf.playtime.core.service.PayCheckService
import dev.slne.surf.playtime.fallback.repository.playtimeRepository
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(PayCheckService::class)
class PayCheckServiceImpl : PayCheckService, Services.Fallback {
    val currentServerPlaytime = mutableObject2ObjectMapOf<UUID, Long>()
    lateinit var config: PlaytimeConfig

    override fun create(config: PlaytimeConfig) {
        this.config = config
    }

    override fun getCurrentPlaytime(playerUuid: UUID) = currentServerPlaytime[playerUuid] ?: 0L

    override suspend fun increasePlaytime(playerUuid: UUID, amount: Long) {
        currentServerPlaytime.merge(playerUuid, amount) { old, new -> old + new }.also {
            handleUpdate(playerUuid, getCurrentPlaytime(playerUuid))
        }
    }

    override suspend fun handleUpdate(playerUuid: UUID, newPlaytime: Long) {
        if (!config.paycheck.enabled) {
            return
        }

        val interval = (config.paycheck.intervalMinutes.coerceAtLeast(1)) * 60L

        if (newPlaytime % interval == 0L) {
            transactionBridge.givePaycheck(playerUuid)
        }
    }


    override suspend fun cachePlaytime(playerUuid: UUID) {
        currentServerPlaytime[playerUuid] =
            playtimeRepository.loadPlaytimeSecondsByServer(
                playerUuid,
                SurfServer.current().displayName
            )
    }
}