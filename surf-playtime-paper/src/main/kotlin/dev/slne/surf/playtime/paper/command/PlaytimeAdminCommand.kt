package dev.slne.surf.playtime.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.longArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import dev.slne.surf.playtime.paper.playtimeConfigManager
import dev.slne.surf.playtime.paper.plugin
import kotlinx.coroutines.Deferred
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val germanDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun playtimeAdminCommand() = commandTree("playtimeadmin") {
    withPermission("surf.playtime.command.admin")
    literalArgument("reload") {
        anyExecutor { sender, _ ->
            playtimeConfigManager.reload()

            sender.sendText {
                appendSuccessPrefix()
                success("Die Konfiguration wurde neu geladen.")
            }
        }
    }

    literalArgument("streak") {
        literalArgument("recalculateall") {
            anyExecutor { sender, _ ->
                sender.sendText {
                    appendInfoPrefix()
                    info("Die Login-Streaks aller Spieler werden neu berechnet...")
                }

                plugin.launch {
                    val count = PlaytimeStreakService.recalculateAllPlaytimeStreaks()

                    sender.sendText {
                        appendSuccessPrefix()
                        success("Die Login-Streaks von ")
                        variableValue("$count Spielern")
                        success(" wurden neu berechnet.")
                    }
                }
            }
        }

        literalArgument("recalculate") {
            surfOfflinePlayerArgument("player") {
                anyExecutor { sender, args ->
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

                        val streak =
                            PlaytimeStreakService.recalculatePlaytimeStreak(targetPlayer.uuid)

                        if (streak == null) {
                            sender.sendText {
                                appendErrorPrefix()
                                error("Für ")
                                variableValue(targetPlayer.lastKnownName ?: "Unbekannt")
                                error(" wurden keine Sessions gefunden.")
                            }
                            return@launch
                        }

                        PlaytimeStreakService.invalidateCache(targetPlayer.uuid)

                        sender.sendText {
                            appendSuccessPrefix()
                            success("Die Login-Streak von ")
                            variableValue(targetPlayer.lastKnownName ?: "Unbekannt")
                            success(" wurde neu berechnet: ")
                            variableValue("${streak.currentLoginStreak} Tage")
                            success(" (Best: ")
                            variableValue("${streak.longestLoginStreak} Tage")
                            success(").")
                        }
                    }
                }
            }
        }
    }

    literalArgument("streakpause") {
        literalArgument("create") {
            stringArgument("von") {
                stringArgument("bis") {
                    anyExecutor { sender, args ->
                        val von: String by args
                        val bis: String by args

                        val startDate = parseDate(von)
                        val endDate = parseDate(bis)

                        if (startDate == null || endDate == null) {
                            sender.sendText {
                                appendErrorPrefix()
                                error("Ungültiges Datum. Erlaubte Formate: ")
                                variableValue("31.12.2026")
                                error(" oder ")
                                variableValue("2026-12-31")
                                error(".")
                            }
                            return@anyExecutor
                        }

                        if (endDate.isBefore(startDate)) {
                            sender.sendText {
                                appendErrorPrefix()
                                error("Das Enddatum darf nicht vor dem Startdatum liegen.")
                            }
                            return@anyExecutor
                        }

                        plugin.launch {
                            val pause =
                                PlaytimeStreakService.createStreakPause(startDate, endDate)

                            sender.sendText {
                                appendSuccessPrefix()
                                success("Die Streak-Pause ")
                                variableValue("#${pause.id}")
                                success(" wurde von ")
                                variableValue(pause.startDate.format(germanDateFormat))
                                success(" bis ")
                                variableValue(pause.endDate.format(germanDateFormat))
                                success(" eingetragen.")
                            }
                        }
                    }
                }
            }
        }

        literalArgument("delete") {
            longArgument("id") {
                anyExecutor { sender, args ->
                    val id: Long by args

                    plugin.launch {
                        val deleted = PlaytimeStreakService.deleteStreakPause(id)

                        if (deleted) {
                            sender.sendText {
                                appendSuccessPrefix()
                                success("Die Streak-Pause ")
                                variableValue("#$id")
                                success(" wurde gelöscht.")
                            }
                        } else {
                            sender.sendText {
                                appendErrorPrefix()
                                error("Es wurde keine Streak-Pause mit der ID ")
                                variableValue("#$id")
                                error(" gefunden.")
                            }
                        }
                    }
                }
            }
        }

        literalArgument("list") {
            anyExecutor { sender, _ ->
                plugin.launch {
                    val pauses = PlaytimeStreakService.loadStreakPauses()

                    if (pauses.isEmpty()) {
                        sender.sendText {
                            appendInfoPrefix()
                            info("Es sind keine Streak-Pausen eingetragen.")
                        }
                        return@launch
                    }

                    sender.sendText {
                        appendInfoPrefix()
                        info("Eingetragene Streak-Pausen:")

                        for (pause in pauses) {
                            appendNewline().appendInfoPrefix()
                            spacer("- ")
                            variableKey("#${pause.id}")
                            spacer(": ")
                            variableValue(pause.startDate.format(germanDateFormat))
                            spacer(" - ")
                            variableValue(pause.endDate.format(germanDateFormat))
                        }
                    }
                }
            }
        }
    }
}

private fun parseDate(input: String): LocalDate? = try {
    LocalDate.parse(input)
} catch (_: DateTimeParseException) {
    try {
        LocalDate.parse(input, germanDateFormat)
    } catch (_: DateTimeParseException) {
        null
    }
}
