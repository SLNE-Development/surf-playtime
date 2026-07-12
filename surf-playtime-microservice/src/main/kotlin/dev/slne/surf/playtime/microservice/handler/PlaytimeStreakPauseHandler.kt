package dev.slne.surf.playtime.microservice.handler

import dev.slne.surf.playtime.core.common.rabbit.packet.request.CreatePlaytimeStreakPauseRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.DeletePlaytimeStreakPauseRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadPlaytimeStreakPausesRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimeStreakPauseResponsePacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.PlaytimeStreakPausesResponsePacket
import dev.slne.surf.playtime.microservice.repository.PlaytimeStreakPauseRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object PlaytimeStreakPauseHandler {
    @RabbitHandler
    fun handleLoadPausesRequest(request: LoadPlaytimeStreakPausesRequestPacket) = request.launch {
        request.respond(
            PlaytimeStreakPausesResponsePacket(PlaytimeStreakPauseRepository.findAllPauses())
        )
    }

    @RabbitHandler
    fun handleCreatePauseRequest(request: CreatePlaytimeStreakPauseRequestPacket) =
        request.launch {
            request.respond(
                PlaytimeStreakPauseResponsePacket(
                    PlaytimeStreakPauseRepository.createPause(request.startDate, request.endDate)
                )
            )
        }

    @RabbitHandler
    fun handleDeletePauseRequest(request: DeletePlaytimeStreakPauseRequestPacket) =
        request.launch {
            request.respond(
                PrimitiveResponse.BooleanResponsePacket(
                    PlaytimeStreakPauseRepository.deletePause(request.pauseId)
                )
            )
        }
}
