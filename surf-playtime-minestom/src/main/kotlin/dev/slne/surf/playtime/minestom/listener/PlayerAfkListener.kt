package dev.slne.surf.playtime.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.extension.GlobalEventHandler
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.playtime.api.minestom.event.AfkStateChangeEvent
import dev.slne.surf.playtime.core.client.afk.AfkTracker
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent

/**
 * Turns player activity into afk state changes.
 */
class PlayerAfkListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerMoveEvent> { event ->
            val position = event.player.position
            val newPosition = event.newPosition

            if (newPosition.yaw == position.yaw && newPosition.pitch == position.pitch) {
                return@addListener
            }

            markActive(event.player)
        }

        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            AfkTracker.track(event.player.uuid)
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            AfkTracker.forget(event.player.uuid)
        }
    }
}

private fun markActive(player: Player) {
    if (AfkTracker.markActive(player.uuid)) {
        applyAfkState(player, false)
    }
}

/**
 * Moves every player that has been idle for long enough into the afk state.
 */
internal fun checkAfkStates() {
    val now = System.nanoTime()

    for (uuid in AfkTracker.idlePlayers(now)) {
        if (!AfkTracker.markAfkIfStillIdle(uuid, now)) continue

        val player = ConnectionManager.getOnlinePlayerByUuid(uuid)
        if (player == null) {
            AfkTracker.forget(uuid)
            continue
        }

        applyAfkState(player, true)
    }
}

private fun applyAfkState(player: Player, isAfk: Boolean) {
    AfkTracker.applyState(player.uuid, isAfk) {
        GlobalEventHandler.call(AfkStateChangeEvent(player, isAfk))
    }
}
