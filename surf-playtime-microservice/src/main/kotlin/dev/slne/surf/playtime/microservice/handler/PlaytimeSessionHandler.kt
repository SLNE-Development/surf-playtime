package dev.slne.surf.playtime.microservice.handler

import dev.slne.surf.playtime.core.common.rabbit.packet.request.*
import dev.slne.surf.playtime.core.common.rabbit.packet.response.BooleanResponsePacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.LongResponsePacket
import dev.slne.surf.playtime.core.common.rabbit.packet.response.ManySessionsResponsePacket
import dev.slne.surf.playtime.microservice.repository.playtimeRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import kotlinx.coroutines.launch

object PlaytimeSessionHandler {
    @RabbitHandler
    fun handleSaveSessionRequest(request: SaveSessionRequestPacket) = request.launch {
        request.respond(BooleanResponsePacket(playtimeRepository.saveSession(request.session)))
    }

    @RabbitHandler
    fun handleLoadSessionsByPlayerUuidRequest(request: LoadSessionsByPlayerUuidRequestPacket) =
        request.launch {
            request.respond(ManySessionsResponsePacket(playtimeRepository.loadSessions(request.playerUuid)))
        }

    @RabbitHandler
    fun handleLoadSessionsByServerRequest(request: LoadSessionsByPlayerUuidAndServerNameRequestPacket) =
        request.launch {
            request.respond(
                ManySessionsResponsePacket(
                    playtimeRepository.loadSessionsByServer(
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
                    playtimeRepository.loadSessionsByCategory(
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
                LongResponsePacket(
                    playtimeRepository.loadPlaytimeSecondsByServer(
                        request.playerUuid,
                        request.serverName
                    )
                )
            )
        }

}