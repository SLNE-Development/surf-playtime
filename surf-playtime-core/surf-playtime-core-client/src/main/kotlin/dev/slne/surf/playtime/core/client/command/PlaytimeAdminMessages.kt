package dev.slne.surf.playtime.core.client.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import net.kyori.adventure.audience.Audience

fun Audience.sendConfigurationReloaded() = sendText {
    appendSuccessPrefix()
    success("Die Konfiguration wurde neu geladen.")
}

fun Audience.sendRecalculateAllStarted() = sendText {
    appendInfoPrefix()
    info("Die Login-Streaks aller Spieler werden neu berechnet...")
}

fun Audience.sendRecalculateAllFinished(count: Int) = sendText {
    appendSuccessPrefix()
    success("Die Login-Streaks von ")
    variableValue("$count Spielern")
    success(" wurden neu berechnet.")
}

fun Audience.sendNoSessionsFound(playerName: String) = sendText {
    appendErrorPrefix()
    error("Für ")
    variableValue(playerName)
    error(" wurden keine Sessions gefunden.")
}

fun Audience.sendStreakRecalculated(
    playerName: String,
    currentLoginStreak: Int,
    longestLoginStreak: Int
) = sendText {
    appendSuccessPrefix()
    success("Die Login-Streak von ")
    variableValue(playerName)
    success(" wurde neu berechnet: ")
    variableValue("$currentLoginStreak Tage")
    success(" (Best: ")
    variableValue("$longestLoginStreak Tage")
    success(").")
}

fun Audience.sendInvalidDate() = sendText {
    appendErrorPrefix()
    error("Ungültiges Datum. Erlaubte Formate: ")
    variableValue("31.12.2026")
    error(" oder ")
    variableValue("2026-12-31")
    error(".")
}

fun Audience.sendEndDateBeforeStartDate() = sendText {
    appendErrorPrefix()
    error("Das Enddatum darf nicht vor dem Startdatum liegen.")
}

fun Audience.sendStreakPauseCreated(pause: PlaytimeStreakPause) = sendText {
    appendSuccessPrefix()
    success("Die Streak-Pause ")
    variableValue("#${pause.id}")
    success(" wurde von ")
    variableValue(pause.startDate.format(germanDateFormat))
    success(" bis ")
    variableValue(pause.endDate.format(germanDateFormat))
    success(" eingetragen.")
}

fun Audience.sendStreakPauseDeleted(pauseId: Long) = sendText {
    appendSuccessPrefix()
    success("Die Streak-Pause ")
    variableValue("#$pauseId")
    success(" wurde gelöscht.")
}

fun Audience.sendStreakPauseNotFound(pauseId: Long) = sendText {
    appendErrorPrefix()
    error("Es wurde keine Streak-Pause mit der ID ")
    variableValue("#$pauseId")
    error(" gefunden.")
}

fun Audience.sendNoStreakPauses() = sendText {
    appendInfoPrefix()
    info("Es sind keine Streak-Pausen eingetragen.")
}

fun Audience.sendStreakPauseList(pauses: List<PlaytimeStreakPause>) = sendText {
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
