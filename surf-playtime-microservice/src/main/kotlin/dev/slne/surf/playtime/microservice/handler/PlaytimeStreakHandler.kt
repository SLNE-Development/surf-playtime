package dev.slne.surf.playtime.microservice.handler

import dev.slne.surf.playtime.core.common.rabbit.packet.request.CalculatePlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadPlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.SavePlaytimeStreakRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.IntResponsePacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimeStreakResponsePacket
import dev.slne.surf.playtime.microservice.repository.PlaytimeStreakRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object PlaytimeStreakHandler {
    @RabbitHandler
    fun handleLoadStreakRequest(requestPacket: LoadPlaytimeStreakRequestPacket) =
        requestPacket.launch {
            requestPacket.respond(
                PlaytimeStreakResponsePacket(
                    PlaytimeStreakRepository.findStreakInformation(requestPacket.playerUuid)
                )
            )
        }

    @RabbitHandler
    fun handleIncreaseStreakRequest(request: SavePlaytimeStreakRequestPacket) = request.launch {
        PlaytimeStreakRepository.saveStreak(request.playerUuid, request.streak, request.date)
        request.respond(PrimitiveResponse.BooleanResponsePacket(true))
    }

    @RabbitHandler
    fun handleCalculateStreakRequest(request: CalculatePlaytimeStreakRequestPacket) =
        request.launch {
            request.respond(IntResponsePacket(PlaytimeStreakRepository.calculateStreak(request.playerUuid)))
        }
}