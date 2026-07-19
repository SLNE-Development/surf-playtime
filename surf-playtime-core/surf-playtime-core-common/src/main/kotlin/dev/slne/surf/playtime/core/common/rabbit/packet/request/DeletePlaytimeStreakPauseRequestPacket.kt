package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.serialization.Serializable

@Serializable
data class DeletePlaytimeStreakPauseRequestPacket(
    val pauseId: Long
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()
