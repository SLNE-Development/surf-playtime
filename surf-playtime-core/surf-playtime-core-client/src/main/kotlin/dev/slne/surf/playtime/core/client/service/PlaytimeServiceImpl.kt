package dev.slne.surf.playtime.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.toObjectSet
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.client.ClientPlaytimeInstance
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidAndServerCategoryRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidAndServerNameRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.LoadSessionsByPlayerUuidRequestPacket
import dev.slne.surf.playtime.core.common.rabbit.packet.request.SaveSessionRequestPacket
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@AutoService(PlaytimeService::class)
class PlaytimeServiceImpl : PlaytimeService {
    private val sessionsById = ConcurrentHashMap<UUID, PlaytimeSession>()
    private val sessionsByPlayer = ConcurrentHashMap<UUID, List<PlaytimeSession>>()

    private val sessionsView: Collection<PlaytimeSession> =
        Collections.unmodifiableCollection(sessionsById.values)

    override val activePlaytimeSessions: Set<PlaytimeSession> get() = sessionsById.values.toSet()

    override val activeSessionsView: Collection<PlaytimeSession> get() = sessionsView

    override fun activeSessionsOf(playerUuid: UUID): List<PlaytimeSession> =
        sessionsByPlayer[playerUuid] ?: emptyList()

    override suspend fun saveSession(session: PlaytimeSession) {
        ClientPlaytimeInstance.rabbitApi.sendRequest(SaveSessionRequestPacket(session))
    }

    override suspend fun loadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        ClientPlaytimeInstance.rabbitApi.sendRequest(LoadSessionsByPlayerUuidRequestPacket(playerUuid)).sessions.toObjectSet()

    override fun cacheSession(session: PlaytimeSession) {
        sessionsById[session.sessionId] = session

        sessionsByPlayer.compute(session.playerUuid) { _, sessions ->
            when {
                sessions == null -> listOf(session)
                sessions.any { it.sessionId == session.sessionId } -> sessions
                else -> sessions + session
            }
        }
    }

    override fun removeCachedSession(sessionId: UUID) {
        val removed = sessionsById.remove(sessionId) ?: return

        sessionsByPlayer.computeIfPresent(removed.playerUuid) { _, sessions ->
            val remaining = sessions.filter { it.sessionId != sessionId }

            remaining.ifEmpty { null }
        }
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
