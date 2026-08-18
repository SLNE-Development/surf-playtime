package dev.slne.surf.playtime.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.playtime.core.client.command.sendPlayerNotFound
import dev.slne.surf.playtime.core.client.command.sendPlaytimeOverview
import dev.slne.surf.playtime.core.client.permission.PlaytimePermissions
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import dev.slne.surf.playtime.paper.plugin
import kotlinx.coroutines.Deferred

fun playtimeCommand() = commandTree("playtime") {
    withAliases("pt")
    withPermission(PlaytimePermissions.COMMAND)

    playerExecutor { player, _ ->
        plugin.launch {
            player.sendPlaytimeOverview(
                sessions = PlaytimeService.getAndLoadSessions(player.uniqueId),
                streak = PlaytimeStreakService.getStreak(player.uniqueId)
            )
        }
    }

    surfOfflinePlayerArgument("player") {
        withPermission(PlaytimePermissions.COMMAND_OTHERS)

        playerExecutor { sender, args ->
            val player: Deferred<SurfPlayer?> by args

            plugin.launch {
                val targetPlayer = player.await()
                if (targetPlayer == null) {
                    sender.sendPlayerNotFound()
                    return@launch
                }

                sender.sendPlaytimeOverview(
                    sessions = PlaytimeService.getAndLoadSessions(targetPlayer.uuid),
                    streak = PlaytimeStreakService.loadOrCalculateStreak(targetPlayer.uuid),
                    targetName = targetPlayer.lastKnownName ?: "Unbekannt"
                )
            }
        }
    }
}
