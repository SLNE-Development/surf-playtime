package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.playtime.api.common.session.PlaytimeFilter
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimePerPlayerResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class LoadPlaytimePerPlayerRequestPacket(
    val filter: PlaytimeFilter
) : RabbitRequestPacket<PlaytimePerPlayerResponsePacket>()
