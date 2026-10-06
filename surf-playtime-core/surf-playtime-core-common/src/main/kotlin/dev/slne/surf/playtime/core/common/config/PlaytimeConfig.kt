package dev.slne.surf.playtime.core.common.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class PlaytimeConfig(
    val paycheck: PaycheckConfig = PaycheckConfig(),
    val eventCoinPayoutConfig: EventCoinPayoutConfig = EventCoinPayoutConfig()
)

@ConfigSerializable
data class PaycheckConfig(
    val enabled: Boolean = false,
    val intervalMinutes: Long = 60,
    val amount: Int = 500,
    val maxBalance: Int = 100000
)

@ConfigSerializable
data class EventCoinPayoutConfig(
    val enabled: Boolean = false,
    val currencyName: String = "halloweencoin",
    val intervalMinutes: Long = 60,
    val amount: Int = 100,
    val maxBalance: Int? = null
)