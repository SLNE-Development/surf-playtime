package dev.slne.surf.playtime.paper.standalone.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.playtime.paper.standalone.plugin
import dev.slne.surf.playtime.paper.standalone.service.PlaytimeSessionService
import org.bukkit.Bukkit

fun playtimeCommand() = commandTree("playtime") {
    withAliases("pt")
    withPermission(PlaytimePermissions.COMMAND)

    playerExecutor { player, _ ->
        plugin.launch {
            player.sendPlaytimeOverview(
                sessions = PlaytimeSessionService.getAndLoadSessions(player.uniqueId)
            )
        }
    }

    stringArgument("player") {
        withPermission(PlaytimePermissions.COMMAND_OTHERS)
        replaceSuggestions(ArgumentSuggestions.strings { _ ->
            Bukkit.getOnlinePlayers().map { it.name }.toTypedArray()
        })

        playerExecutor { sender, args ->
            val player: String by args

            val targetPlayer =
                Bukkit.getPlayerExact(player) ?: Bukkit.getOfflinePlayerIfCached(player)
            if (targetPlayer == null) {
                sender.sendPlayerNotFound()
                return@playerExecutor
            }

            plugin.launch {
                sender.sendPlaytimeOverview(
                    sessions = PlaytimeSessionService.getAndLoadSessions(targetPlayer.uniqueId),
                    targetName = targetPlayer.name ?: player
                )
            }
        }
    }
}
