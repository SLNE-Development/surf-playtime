package dev.slne.surf.playtime.core.client.command

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val germanDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/**
 * Parses [input] as an ISO date, falling back to the german `dd.MM.yyyy` format.
 *
 * Returns `null` when [input] matches neither format.
 */
fun parseDate(input: String): LocalDate? = try {
    LocalDate.parse(input)
} catch (_: DateTimeParseException) {
    try {
        LocalDate.parse(input, germanDateFormat)
    } catch (_: DateTimeParseException) {
        null
    }
}

/**
 * Formats a duration in seconds as `1h 02m 03s`, omitting leading units that are zero.
 */
fun Long.formatSeconds(): String {
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
