package dev.slne.surf.playtime.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.core.common.service.PlaytimeService
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import dev.slne.surf.playtime.paper.plugin
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Deferred

fun playtimeCommand() = commandTree("playtime") {
    withAliases("pt")
    withPermission("surf.playtime.command")

    playerExecutor { player, _ ->
        plugin.launch {
            val playtime = PlaytimeService.getAndLoadSessions(player.uniqueId)
            val summedPlaytime = playtime.sumPlaytime()

            player.sendText {
                appendNewline()
                appendInfoPrefix()
                info("Deine Spielzeit")
                appendNewline().appendInfoPrefix()
                append {
                    appendNewline().appendInfoPrefix()
                    variableKey("Gesamt")
                    spacer(": ")
                    variableValue(playtime.sumOf { it.durationSeconds }.formatSeconds())
                }

                val (current, max) =
                    PlaytimeStreakService.getStreak(player.uniqueId)
                        ?.let { it.currentLoginStreak to it.longestLoginStreak }
                        ?: (0 to 0)

                append {
                    appendNewline().appendInfoPrefix()
                    variableKey("Login-Streak")
                    spacer(": ")
                    variableValue(if (current > 0) "$current Tage (Max: $max Tage)" else "Keine Streak")
                }
                appendNewline().appendInfoPrefix()
                for ((group, groupServer) in summedPlaytime) {
                    append {
                        appendNewline().appendInfoPrefix()
                        spacer("- ")
                        variableKey(group)
                        spacer(": ")
                        variableValue(playtime.sumByCategory(group).formatSeconds())

                        for ((serverName, playtime) in groupServer) {
                            append {
                                appendNewline().appendInfoPrefix()
                                text("    ")
                                variableKey(serverName)
                                spacer(": ")
                                variableValue(playtime.formatSeconds())
                            }
                        }
                        appendNewline().appendInfoPrefix()
                    }
                }
            }
        }
    }

    surfOfflinePlayerArgument("player") {
        withPermission("surf.playtime.command.others")

        playerExecutor { sender, args ->
            val player: Deferred<SurfPlayer?> by args

            plugin.launch {
                val targetPlayer = player.await()
                if (targetPlayer == null) {
                    sender.sendText {
                        appendErrorPrefix()
                        error("Spieler wurde nicht gefunden.")
                    }
                    return@launch
                }

                val playtime = PlaytimeService.getAndLoadSessions(targetPlayer.uuid)
                val summedPlaytime = playtime.sumPlaytime()

                sender.sendText {
                    appendNewline()
                    appendInfoPrefix()
                    info("Spielzeit von ")
                    variableValue(targetPlayer.lastKnownName ?: "Unbekannt")
                    appendNewline().appendInfoPrefix()
                    append {
                        appendNewline().appendInfoPrefix()
                        variableKey("Gesamt")
                        spacer(": ")
                        variableValue(playtime.sumOf { it.durationSeconds }.formatSeconds())
                    }
                    appendNewline().appendInfoPrefix()
                    for ((group, groupServer) in summedPlaytime) {
                        append {
                            appendNewline().appendInfoPrefix()
                            spacer("- ")
                            variableKey(group)
                            spacer(": ")
                            variableValue(playtime.sumByCategory(group).formatSeconds())

                            for ((serverName, playtime) in groupServer) {
                                append {
                                    appendNewline().appendInfoPrefix()
                                    text("    ")
                                    variableKey(serverName)
                                    spacer(": ")
                                    variableValue(playtime.formatSeconds())
                                }
                            }
                            appendNewline().appendInfoPrefix()
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

private fun Long.formatSeconds(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when {
        hours > 0 -> {
            String.format("%dh %02dm %02ds", hours, minutes, seconds)
        }

        minutes > 0 -> {
            String.format("%dm %02ds", minutes, seconds)
        }

        else -> {
            String.format("%ds", seconds)
        }
    }
}
