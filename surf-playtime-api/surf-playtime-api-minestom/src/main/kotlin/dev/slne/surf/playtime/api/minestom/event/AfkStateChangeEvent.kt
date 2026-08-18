package dev.slne.surf.playtime.api.minestom.event

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.PlayerEvent

/**
 * Called after the afk state of a player changed.
 */
class AfkStateChangeEvent(
    private val player: Player,
    val isAfk: Boolean
) : PlayerEvent {
    override fun getPlayer() = player
}
