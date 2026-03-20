package dev.slne.surf.playtime.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class LongResponsePacket(
    val result: Long
) : RabbitResponsePacket()
