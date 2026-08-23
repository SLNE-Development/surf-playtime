package dev.slne.surf.playtime.core.client.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.api.common.session.PlaytimeSession
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import net.kyori.adventure.audience.Audience

/**
 * Sends the playtime overview of a player.
 *
 * [targetName] names the player the overview belongs to; passing `null` addresses the receiver
 * as the owner of the shown playtime.
 */
fun Audience.sendPlaytimeOverview(
    sessions: Collection<PlaytimeSession>,
    streak: PlaytimeStreak.SimpleStreak?,
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

        val (current, max) = streak
            ?.let { it.currentLoginStreak to it.longestLoginStreak }
            ?: (0 to 0)

        append {
            appendNewline().appendInfoPrefix()
            variableKey("Login-Streak")
            spacer(": ")
            if (current > 0) {
                variableValue("$current Tage")
            } else {
                variableValue("Keine Streak")
            }
            variableValue(" (Best: $max Tage)")
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

/**
 * Tells the receiver that the requested player does not exist.
 */
fun Audience.sendPlayerNotFound() = sendText {
    appendErrorPrefix()
    error("Spieler wurde nicht gefunden.")
}
