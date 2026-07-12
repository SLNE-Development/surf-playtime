package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.datetime.date.local.SerializableLocalDate
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimeStreakPauseResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaytimeStreakPauseRequestPacket(
    val startDate: SerializableLocalDate,
    val endDate: SerializableLocalDate
) : RabbitRequestPacket<PlaytimeStreakPauseResponsePacket>()
