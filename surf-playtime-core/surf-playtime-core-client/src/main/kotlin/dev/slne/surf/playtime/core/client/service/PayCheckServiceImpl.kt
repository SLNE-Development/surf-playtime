package dev.slne.surf.playtime.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.client.session.PlayerSessionEpochs
import dev.slne.surf.playtime.core.common.bridge.transactionBridge
import dev.slne.surf.playtime.core.common.config.PlaytimeConfig
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSecondsByPlayerUuidAndServerNameRequestPacket
import dev.slne.surf.playtime.core.common.service.PayCheckService
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@AutoService(PayCheckService::class)
class PayCheckServiceImpl : PayCheckService {
    private val currentServerPlaytime = ConcurrentHashMap<UUID, AtomicLong>()

    @Volatile
    private lateinit var config: PlaytimeConfig

    override fun create(config: PlaytimeConfig) {
        this.config = config
    }

    override fun getCurrentPlaytime(playerUuid: UUID) =
        currentServerPlaytime[playerUuid]?.get() ?: 0L

    override suspend fun increasePlaytime(playerUuid: UUID, amount: Long) {
        val counter = currentServerPlaytime[playerUuid] ?: createCounter(playerUuid) ?: return

        handleUpdate(playerUuid, counter.addAndGet(amount))
    }

    override suspend fun handleUpdate(playerUuid: UUID, newPlaytime: Long) {
        val paycheck = config.paycheck

        if (!paycheck.enabled) {
            return
        }

        val interval = paycheck.intervalMinutes.coerceAtLeast(1) * 60L

        if (newPlaytime % interval == 0L) {
            transactionBridge.givePaycheck(playerUuid)
        }
    }

    override suspend fun cachePlaytime(playerUuid: UUID) {
        val epoch = PlayerSessionEpochs.current(playerUuid)

        val seconds = ClientPlaytimeInstance.rabbitApi.sendRequest(
            LoadSecondsByPlayerUuidAndServerNameRequestPacket(
                playerUuid,
                SurfServer.current().displayName
            )
        ).value

        PlayerSessionEpochs.ifCurrent(playerUuid, epoch) {
            counterOf(playerUuid).set(seconds)
        }
    }

    override fun invalidateCache(playerUuid: UUID) {
        currentServerPlaytime.remove(playerUuid)
    }

    private fun createCounter(playerUuid: UUID): AtomicLong? {
        var counter: AtomicLong? = null

        PlayerSessionEpochs.ifTracked(playerUuid) { counter = counterOf(playerUuid) }

        return counter
    }

    private fun counterOf(playerUuid: UUID) =
        currentServerPlaytime.computeIfAbsent(playerUuid) { AtomicLong() }
}
