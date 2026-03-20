package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.common.rabbit.packet.response.BooleanResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class SaveSessionRequestPacket(
    val session: PlaytimeSession
) : RabbitRequestPacket<BooleanResponsePacket>()