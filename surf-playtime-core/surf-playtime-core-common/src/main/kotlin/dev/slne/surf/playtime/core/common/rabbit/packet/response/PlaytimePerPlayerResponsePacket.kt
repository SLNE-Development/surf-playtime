package dev.slne.surf.playtime.core.common.rabbit.packet.response

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class PlaytimePerPlayerResponsePacket(
    val playtime: Map<SerializableUUID, Long>
) : RabbitResponsePacket()
