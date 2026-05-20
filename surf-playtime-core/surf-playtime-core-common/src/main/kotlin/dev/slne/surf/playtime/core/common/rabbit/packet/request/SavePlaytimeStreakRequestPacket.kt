package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.datetime.date.local.SerializableLocalDate
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class SavePlaytimeStreakRequestPacket(
    val playerUuid: SerializableUUID,
    val streak: Int,
    val date: SerializableLocalDate
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
