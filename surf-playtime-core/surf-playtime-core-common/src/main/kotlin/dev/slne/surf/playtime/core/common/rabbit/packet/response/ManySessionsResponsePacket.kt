package dev.slne.surf.playtime.core.common.rabbit.packet.response

import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class ManySessionsResponsePacket(
    val sessions: Set<PlaytimeSession>
) : RabbitResponsePacket()
