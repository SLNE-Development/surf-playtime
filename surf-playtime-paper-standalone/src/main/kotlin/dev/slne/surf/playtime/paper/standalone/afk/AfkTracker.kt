package dev.slne.surf.playtime.paper.standalone.afk

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.emptyObjectSet
import dev.slne.surf.playtime.paper.standalone.config.playtimeConfig
import dev.slne.surf.playtime.paper.standalone.service.PlaytimeSessionService
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

object AfkTracker {
    private val afkTimeNanos get() = playtimeConfig.afkTimeoutSeconds.seconds.inWholeNanoseconds
    private val states = ConcurrentHashMap<UUID, AfkState>()

    fun track(playerUuid: UUID) {
        states[playerUuid] = AfkState(System.nanoTime())
    }

    fun forget(playerUuid: UUID) {
        states.remove(playerUuid)
    }

    fun isAfk(playerUuid: UUID) = states[playerUuid]?.afk?.get() == true

    fun markActive(playerUuid: UUID): Boolean {
        val state = states[playerUuid] ?: return false

        state.lastActivityNanos = System.nanoTime()

        return state.afk.get() && state.afk.compareAndSet(true, false)
    }

    @Suppress("JavaMapForEach")
    fun idlePlayers(now: Long): Set<UUID> {
        val afkTimeNanos = afkTimeNanos
        var idle: ObjectOpenHashSet<UUID>? = null

        states.forEach { uuid, state ->
            if (!state.afk.get() && now - state.lastActivityNanos >= afkTimeNanos) {
                (idle ?: ObjectOpenHashSet<UUID>().also { idle = it }).add(uuid)
            }
        }

        return idle ?: emptyObjectSet()
    }

    fun markAfkIfStillIdle(playerUuid: UUID, now: Long): Boolean {
        val state = states[playerUuid] ?: return false
        val afkTimeNanos = afkTimeNanos

        if (now - state.lastActivityNanos < afkTimeNanos) return false
        if (!state.afk.compareAndSet(false, true)) return false

        if (now - state.lastActivityNanos < afkTimeNanos) {
            state.afk.compareAndSet(true, false)
            return false
        }

        return true
    }

    fun applyState(
        playerUuid: UUID,
        isAfk: Boolean,
        onChanged: () -> Unit = {},
    ): Boolean {
        val state = states[playerUuid] ?: return false
        if (state.afk.get() != isAfk) return false

        if (isAfk) {
            PlaytimeSessionService.endSessionAsync(playerUuid)
        } else if (PlaytimeSessionService.activeSessionOf(playerUuid) == null) {
            PlaytimeSessionService.startSession(playerUuid)
        }

        onChanged()

        Bukkit.getPlayer(playerUuid)?.sendText {
            appendInfoPrefix()
            info("Du bist nun ")

            if (isAfk) {
                info("AFK.")
            } else {
                info("nicht mehr AFK.")
            }
        }

        return true
    }
}
