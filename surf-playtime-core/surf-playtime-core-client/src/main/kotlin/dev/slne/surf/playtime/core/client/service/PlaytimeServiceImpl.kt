package dev.slne.surf.playtime.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.toObjectSet
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidAndServerCategoryRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidAndServerNameRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.SaveSessionRequestPacket
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import io.ktor.util.collections.*
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

@AutoService(PlaytimeService::class)
class PlaytimeServiceImpl : PlaytimeService {
    private val _sessions = ConcurrentMap<UUID, PlaytimeSession>()

    override val activePlaytimeSessions get() = _sessions.values.toSet()
    override suspend fun saveSession(session: PlaytimeSession) {
        ClientPlaytimeInstance.rabbitApi.sendRequest(SaveSessionRequestPacket(session))
    }


    override suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        ClientPlaytimeInstance.rabbitApi.sendRequest(LoadSessionsByPlayerUuidRequestPacket(playerUuid)).sessions.toObjectSet()

    override fun cacheSession(session: PlaytimeSession) {
        _sessions[session.sessionId] = session
    }

    override fun removeCachedSession(sessionId: UUID) {
        _sessions.remove(sessionId)
    }

    override suspend fun loadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = ClientPlaytimeInstance.rabbitApi.sendRequest(
        LoadSessionsByPlayerUuidAndServerNameRequestPacket(playerUuid, serverName)
    ).sessions.toObjectSet()

    override suspend fun loadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession> = ClientPlaytimeInstance.rabbitApi.sendRequest(
        LoadSessionsByPlayerUuidAndServerCategoryRequestPacket(playerUuid, category)
    ).sessions.toObjectSet()
}