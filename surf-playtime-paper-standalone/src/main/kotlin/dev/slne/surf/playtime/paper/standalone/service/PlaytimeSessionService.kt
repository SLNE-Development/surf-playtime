package dev.slne.surf.playtime.paper.standalone.service

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.database.repository.PlaytimeRepository
import dev.slne.surf.playtime.paper.standalone.config.playtimeConfig
import dev.slne.surf.playtime.paper.standalone.plugin
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlaytimeSessionService {
    private val sessionsByPlayer = ConcurrentHashMap<UUID, PlaytimeSession>()

    fun activeSessionOf(playerUuid: UUID): PlaytimeSession? = sessionsByPlayer[playerUuid]

    fun startSession(playerUuid: UUID) {
        val now = LocalDateTime.now()

        sessionsByPlayer[playerUuid] = PlaytimeSession(
            playerUuid = playerUuid,
            sessionId = UUID.randomUUID(),
            server = playtimeConfig.serverName,
            category = playtimeConfig.serverCategory,
            startTime = now,
            endTime = now
        )
    }

    suspend fun endSession(playerUuid: UUID) {
        val session = sessionsByPlayer.remove(playerUuid) ?: return

        session.endTime = LocalDateTime.now()
        PlaytimeRepository.saveSession(session)
    }

    fun endSessionAsync(playerUuid: UUID) {
        val session = sessionsByPlayer.remove(playerUuid) ?: return

        session.endTime = LocalDateTime.now()
        plugin.launch {
            PlaytimeRepository.saveSession(session)
        }
    }

    fun updateAllActiveSessions() {
        val now = LocalDateTime.now()

        for (session in sessionsByPlayer.values) {
            session.endTime = now
        }
    }

    suspend fun flushAll(): Unit = coroutineScope {
        val sessions = sessionsByPlayer.values.toList()

        if (sessions.isEmpty()) {
            return@coroutineScope
        }

        val semaphore = Semaphore(32)
        val now = LocalDateTime.now()

        for (session in sessions) {
            launch {
                semaphore.withPermit {
                    PlaytimeRepository.saveSession(session.apply { endTime = now })
                }
            }
        }
    }

    suspend fun getAndLoadSessions(playerUuid: UUID): ObjectSet<PlaytimeSession> =
        mergeActive(playerUuid, PlaytimeRepository.loadSessions(playerUuid))

    suspend fun getAndLoadSessionsByServer(
        playerUuid: UUID,
        serverName: String
    ): ObjectSet<PlaytimeSession> = mergeActive(
        playerUuid,
        PlaytimeRepository.loadSessionsByServer(playerUuid, serverName)
    ) { it.server == serverName }

    suspend fun getAndLoadSessionsByCategory(
        playerUuid: UUID,
        category: String
    ): ObjectSet<PlaytimeSession> = mergeActive(
        playerUuid,
        PlaytimeRepository.loadSessionsByCategory(playerUuid, category)
    ) { it.category == category }

    private fun mergeActive(
        playerUuid: UUID,
        loadedSessions: Collection<PlaytimeSession>,
        filter: (PlaytimeSession) -> Boolean = { true }
    ): ObjectSet<PlaytimeSession> {
        val activeSession = activeSessionOf(playerUuid)?.takeIf(filter)
        val result = mutableObjectSetOf<PlaytimeSession>(loadedSessions.size + 1)

        if (activeSession != null) {
            result.add(activeSession)
        }

        for (session in loadedSessions) {
            if (session.sessionId != activeSession?.sessionId) {
                result.add(session)
            }
        }

        return result
    }
}
