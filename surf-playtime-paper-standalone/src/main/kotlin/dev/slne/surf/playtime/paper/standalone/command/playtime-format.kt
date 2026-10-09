package dev.slne.surf.playtime.paper.standalone.command

import dev.slne.surf.playtime.api.common.session.PlaytimeSession

fun Long.formatSeconds(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when {
        hours > 0 -> String.format("%dh %02dm %02ds", hours, minutes, seconds)
        minutes > 0 -> String.format("%dm %02ds", minutes, seconds)
        else -> String.format("%ds", seconds)
    }
}

fun Collection<PlaytimeSession>.sumPlaytime(): Map<String, Map<String, Long>> {
    val playtimeByCategory = LinkedHashMap<String, MutableMap<String, Long>>()

    for (session in this) {
        playtimeByCategory
            .getOrPut(session.category) { LinkedHashMap() }
            .merge(session.server, session.durationSeconds, Long::plus)
    }

    return playtimeByCategory
}
