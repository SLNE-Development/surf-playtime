package dev.slne.surf.playtime.microservice.handler

import dev.slne.surf.playtime.core.common.rabbit.packet.request.*
import dev.slne.surf.playtime.core.common.rabbit.packet.response.ManySessionsResponsePacket
import dev.slne.surf.playtime.microservice.repository.PlaytimeRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import kotlinx.coroutines.launch

object PlaytimeSessionHandler {
    @RabbitHandler
    fun handleSaveSessionRequest(request: SaveSessionRequestPacket) = request.launch {
        request.respond(
            PrimitiveResponse.BooleanResponsePacket(
                PlaytimeRepository.saveSession(
                    request.session
                )
            )
        )
    }

    @RabbitHandler
    fun handleLoadSessionsByPlayerUuidRequest(request: LoadSessionsByPlayerUuidRequestPacket) =
        request.launch {
            request.respond(ManySessionsResponsePacket(PlaytimeRepository.loadSessions(request.playerUuid)))
        }

    @RabbitHandler
    fun handleLoadSessionsByServerRequest(request: LoadSessionsByPlayerUuidAndServerNameRequestPacket) =
        request.launch {
            request.respond(
                ManySessionsResponsePacket(
                    PlaytimeRepository.loadSessionsByServer(
                        request.playerUuid,
                        request.serverName
                    )
                )
            )
        }

    @RabbitHandler
    fun handleLoadSessionsByCategoryRequest(request: LoadSessionsByPlayerUuidAndServerCategoryRequestPacket) =
        request.launch {
            request.respond(
                ManySessionsResponsePacket(
                    PlaytimeRepository.loadSessionsByCategory(
                        request.playerUuid,
                        request.serverCategory
                    )
                )
            )
        }

    @RabbitHandler
    fun handleLoadSecondsByServerName(request: LoadSecondsByPlayerUuidAndServerNameRequestPacket) =
        request.launch {
            request.respond(
                PrimitiveResponse.LongResponsePacket(
                    PlaytimeRepository.loadPlaytimeSecondsByServer(
                        request.playerUuid,
                        request.serverName
                    )
                )
            )
        }
}