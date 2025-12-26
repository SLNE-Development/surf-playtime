package dev.slne.surf.playtime.api.redis.event

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AfkStateChangeRedisEvent(
    val uuid: @Contextual UUID,
    val isAfk: Boolean
) : RedisEvent()
