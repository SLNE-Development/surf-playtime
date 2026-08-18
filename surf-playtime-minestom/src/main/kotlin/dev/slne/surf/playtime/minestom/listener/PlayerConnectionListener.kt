package dev.slne.surf.playtime.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.playtime.core.client.session.endPlaytimeSessions
import dev.slne.surf.playtime.core.client.session.handlePlaytimeJoin
import dev.slne.surf.playtime.core.client.session.invalidatePlaytimeCaches
import dev.slne.surf.playtime.core.client.session.startPlaytimeSession
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

/**
 * Opens and closes the playtime session of a player while they are on this server.
 */
class PlayerConnectionListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            val player = event.player
            startPlaytimeSession(player.uuid)

            minestomAsyncScope.launch {
                handlePlaytimeJoin(player.uuid, player)
            }
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            val playerUuid = event.player.uuid
            invalidatePlaytimeCaches(playerUuid)

            minestomAsyncScope.launch {
                endPlaytimeSessions(playerUuid)
            }
        }
    }
}
