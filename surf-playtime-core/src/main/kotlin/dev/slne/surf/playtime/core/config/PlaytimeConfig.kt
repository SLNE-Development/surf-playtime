package dev.slne.surf.playtime.core.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class PlaytimeConfig(
    val paycheck: PaycheckConfig = PaycheckConfig()
)

@ConfigSerializable
data class PaycheckConfig(
    val enabled: Boolean = false,
    val intervalMinutes: Long = 60,
    val amount: Int = 250,
    val maxBalance: Int = 100000
)