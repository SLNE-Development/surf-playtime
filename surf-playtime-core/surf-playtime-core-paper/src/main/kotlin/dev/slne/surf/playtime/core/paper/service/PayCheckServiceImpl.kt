package dev.slne.surf.playtime.core.paper.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.playtime.core.common.bridge.transactionBridge
import dev.slne.surf.playtime.core.common.config.PlaytimeConfig
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSecondsByPlayerUuidAndServerNameRequestPacket
import dev.slne.surf.playtime.core.common.service.PayCheckService
import dev.slne.surf.playtime.core.paper.PaperPlaytimeInstance
import java.util.*

@AutoService(PayCheckService::class)
class PayCheckServiceImpl : PayCheckService {
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
        currentServerPlaytime[playerUuid] = PaperPlaytimeInstance.rabbitApi.sendRequest(
            LoadSecondsByPlayerUuidAndServerNameRequestPacket(
                playerUuid,
                SurfServer.current().displayName
            )
        ).value
    }

    override fun invalidateCache(playerUuid: UUID) {
        currentServerPlaytime.remove(playerUuid)
    }
}