package dev.slne.surf.playtime.core.common.bridge

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

val transactionBridge = requiredService<TransactionBridge>()

interface TransactionBridge {
    suspend fun givePaycheck(playerUuid: UUID)
}