package dev.slne.surf.playtime.paper.hook

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.bridge.TransactionBridge
import dev.slne.surf.playtime.paper.hasTransactionHook
import dev.slne.surf.playtime.paper.plugin
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(TransactionBridge::class)
class TransactionBridgeImpl : TransactionBridge, Services.Fallback {
    override suspend fun givePaycheck(playerUuid: UUID) {
        if (hasTransactionHook) {
            val player = Bukkit.getPlayer(playerUuid) ?: return
            TransactionHook.givePaycheck(player)
        } else {
            plugin.logger.severe("Attempted to give paycheck to player $playerUuid, but no transaction hook is available.")
        }
    }
}