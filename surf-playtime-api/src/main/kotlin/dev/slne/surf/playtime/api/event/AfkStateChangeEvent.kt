package dev.slne.surf.playtime.api.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

class AfkStateChangeEvent(
    val playerUuid: UUID,
    val isAfk: Boolean
) : Event() {
    override fun getHandlers() = handlerList

    companion object {
        val handlerList = HandlerList()
    }
}
