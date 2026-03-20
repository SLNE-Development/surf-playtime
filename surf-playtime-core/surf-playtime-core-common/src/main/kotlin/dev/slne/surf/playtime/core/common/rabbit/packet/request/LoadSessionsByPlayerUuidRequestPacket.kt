package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.playtime.core.common.rabbit.packet.response.ManySessionsResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.surfapi.core.api.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class LoadSessionsByPlayerUuidRequestPacket(
    val playerUuid: SerializableUUID
) : RabbitRequestPacket<ManySessionsResponsePacket>()
