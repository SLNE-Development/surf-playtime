package dev.slne.surf.playtime.api.paper.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.*

class AfkStateChangeEvent(
    val playerUuid: UUID,
    val isAfk: Boolean
) : Event() {
    override fun getHandlers() = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
