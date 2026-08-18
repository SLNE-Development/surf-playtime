package dev.slne.surf.playtime.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.minestom.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.playtime.core.client.command.sendPlayerNotFound
import dev.slne.surf.playtime.core.client.command.sendPlaytimeOverview
import dev.slne.surf.playtime.core.client.permission.PlaytimePermissions
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import kotlinx.coroutines.Deferred

fun playtimeCommand() = commandTree("playtime") {
    withAliases("pt")
    withPermission(PlaytimePermissions.COMMAND)

    playerExecutorSuspend { player, _ ->
        player.sendPlaytimeOverview(
            sessions = PlaytimeService.getAndLoadSessions(player.uuid),
            streak = PlaytimeStreakService.getStreak(player.uuid)
        )
    }

    surfOfflinePlayerArgument("player") {
        withPermission(PlaytimePermissions.COMMAND_OTHERS)

        playerExecutorSuspend { sender, args ->
            val player: Deferred<SurfPlayer?> by args

            val targetPlayer = player.await()
            if (targetPlayer == null) {
                sender.sendPlayerNotFound()
                return@playerExecutorSuspend
            }

            sender.sendPlaytimeOverview(
                sessions = PlaytimeService.getAndLoadSessions(targetPlayer.uuid),
                streak = PlaytimeStreakService.loadOrCalculateStreak(targetPlayer.uuid),
                targetName = targetPlayer.lastKnownName ?: "Unbekannt"
            )
        }
    }
}
