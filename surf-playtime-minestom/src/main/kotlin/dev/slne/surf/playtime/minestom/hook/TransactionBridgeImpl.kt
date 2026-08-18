package dev.slne.surf.playtime.minestom.hook

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.client.paycheck.PayCheckPayout
import dev.slne.surf.playtime.core.client.platform.PlaytimePlatform
import dev.slne.surf.playtime.core.common.bridge.TransactionBridge
import java.util.*

@AutoService(TransactionBridge::class)
class TransactionBridgeImpl : TransactionBridge {
    override suspend fun givePaycheck(playerUuid: UUID) {
        val player = PlaytimePlatform.onlinePlayer(playerUuid) ?: return
        PayCheckPayout.give(playerUuid, player)
    }
}
