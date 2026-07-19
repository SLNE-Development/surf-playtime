package dev.slne.surf.playtime.core.common.rabbit.packet.request

import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimeStreakPausesResponsePacket
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
class LoadPlaytimeStreakPausesRequestPacket :
    RabbitRequestPacket<PlaytimeStreakPausesResponsePacket>()
