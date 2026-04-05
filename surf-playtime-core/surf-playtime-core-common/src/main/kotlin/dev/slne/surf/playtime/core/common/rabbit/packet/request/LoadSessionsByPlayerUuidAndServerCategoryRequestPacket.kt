package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.playtime.core.common.rabbit.packet.response.ManySessionsResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class LoadSessionsByPlayerUuidAndServerCategoryRequestPacket(
    val playerUuid: SerializableUUID,
    val serverCategory: String
) : RabbitRequestPacket<ManySessionsResponsePacket>()
