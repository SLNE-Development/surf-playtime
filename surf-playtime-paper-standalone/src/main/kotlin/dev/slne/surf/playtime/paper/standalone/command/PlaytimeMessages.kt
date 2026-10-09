package dev.slne.surf.playtime.paper.standalone.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import net.kyori.adventure.audience.Audience

fun Audience.sendPlaytimeOverview(
    sessions: Collection<PlaytimeSession>,
    targetName: String? = null,
) {
    val summedPlaytime = sessions.sumPlaytime()

    sendText {
        appendNewline()
        appendInfoPrefix()

        if (targetName == null) {
            info("Deine Spielzeit")
        } else {
            info("Spielzeit von ")
            variableValue(targetName)
        }

        appendNewline().appendInfoPrefix()
        append {
            appendNewline().appendInfoPrefix()
            variableKey("Gesamt")
            spacer(": ")
            variableValue(sessions.sumOf { it.durationSeconds }.formatSeconds())
        }

        appendNewline().appendInfoPrefix()
        for ((group, groupServer) in summedPlaytime) {
            append {
                appendNewline().appendInfoPrefix()
                spacer("- ")
                variableKey(group)
                spacer(": ")
                variableValue(groupServer.values.sum().formatSeconds())

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

fun Audience.sendPlayerNotFound() = sendText {
    appendErrorPrefix()
    error("Spieler wurde nicht gefunden.")
}

fun Audience.sendConfigurationReloaded() = sendText {
    appendSuccessPrefix()
    success("Die Konfiguration wurde neu geladen.")
}
