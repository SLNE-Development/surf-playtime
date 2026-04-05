package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class LoadSecondsByPlayerUuidAndServerNameRequestPacket(
    val playerUuid: SerializableUUID,
    val serverName: String
) : RabbitRequestPacket<PrimitiveResponse.LongResponsePacket>()
