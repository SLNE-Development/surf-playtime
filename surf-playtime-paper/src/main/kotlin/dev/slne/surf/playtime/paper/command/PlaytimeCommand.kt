package dev.slne.surf.playtime.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.playtime.api.session.PlaytimeSession
import dev.slne.surf.playtime.core.service.playtimePlayerService
import dev.slne.surf.playtime.core.service.playtimeService
import dev.slne.surf.playtime.paper.command.argument.playerStringArgument
import dev.slne.surf.playtime.paper.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import it.unimi.dsi.fastutil.objects.ObjectSet

fun playtimeCommand() = commandTree("playtime") {
    withAliases("pt")
    withPermission("surf.playtime.command")

    playerExecutor { player, _ ->
        plugin.launch {
            val playtime = playtimeService.loadSessions(player.uniqueId)
            val summedPlaytime = playtime.sumPlaytime()

            player.sendText {
                appendPrefix()
                info("Deine Spielzeit")
                appendNewPrefixedLine()
                appendNewPrefixedLine {
                    variableKey("Gesamt")
                    spacer(": ")
                    variableValue(playtime.sumOf { it.durationSeconds })
                }
                appendNewPrefixedLine()
                for ((group, groupServer) in summedPlaytime) {
                    appendNewPrefixedLine {
                        spacer("- ")
                        variableKey(group)
                        spacer(": ")
                        variableValue(playtime.sumByCategory(group).toString())

                        for ((serverName, playtime) in groupServer) {
                            appendNewPrefixedLine {
                                text("    ")
                                variableKey(serverName)
                                spacer(": ")
                                variableValue(playtime.toString())
                            }
                        }
                        appendNewPrefixedLine()
                    }
                }
            }
        }
    }

    playerStringArgument("player") {
        withPermission("surf.playtime.command.others")

        playerExecutor { sender, args ->
            val player: String by args

            plugin.launch {
                val targetPlayer = playtimePlayerService.getOrLoadPlayerByName(player)
                if (targetPlayer == null) {
                    sender.sendText {
                        appendPrefix()
                        error("Spieler '$player' wurde nicht gefunden.")
                    }
                    return@launch
                }

                val playtime = playtimeService.loadSessions(targetPlayer.uuid)
                val summedPlaytime = playtime.sumPlaytime()

                sender.sendText {
                    appendPrefix()
                    info("Spielzeit von ")
                    variableValue(targetPlayer.name)
                    appendNewPrefixedLine()
                    appendNewPrefixedLine {
                        variableKey("Gesamt")
                        spacer(": ")
                        variableValue(playtime.sumOf { it.durationSeconds })
                    }
                    appendNewPrefixedLine()
                    for ((group, groupServer) in summedPlaytime) {
                        appendNewPrefixedLine {
                            spacer("- ")
                            variableKey(group)
                            spacer(": ")
                            variableValue(playtime.sumByCategory(group).toString())

                            for ((serverName, playtime) in groupServer) {
                                appendNewPrefixedLine {
                                    text("    ")
                                    variableKey(serverName)
                                    spacer(": ")
                                    variableValue(playtime.toString())
                                }
                            }
                            appendNewPrefixedLine()
                        }
                    }
                }
            }
        }
    }
}

private fun ObjectSet<PlaytimeSession>.sumPlaytime() = this
    .groupBy { it.category }
    .mapValues { (_, sessionsByCategory) ->
        sessionsByCategory
            .groupBy { it.server }
            .mapValues { (_, sessionsByServer) ->
                sessionsByServer.sumOf { it.durationSeconds }
            }
    }

private fun ObjectSet<PlaytimeSession>.sumByCategory(category: String) =
    this
        .filter { it.category == category }
        .sumOf { it.durationSeconds }