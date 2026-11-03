package dev.slne.surf.playtime.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlaytimePerPlayerResponsePacket(
    val playtime: Map<@Contextual UUID, Long>
) : RabbitResponsePacket()
