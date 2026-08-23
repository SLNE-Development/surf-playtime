package dev.slne.surf.playtime.core.client.command

import dev.slne.surf.playtime.api.common.session.PlaytimeSession

/**
 * Groups the playtime of these sessions by server category and, within a category, by server.
 */
fun Collection<PlaytimeSession>.sumPlaytime(): Map<String, Map<String, Long>> {
    val playtimeByCategory = LinkedHashMap<String, MutableMap<String, Long>>()

    for (session in this) {
        playtimeByCategory
            .getOrPut(session.category) { LinkedHashMap() }
            .merge(session.server, session.durationSeconds, Long::plus)
    }

    return playtimeByCategory
}

/**
 * Sums the playtime of these sessions that belong to [category].
 */
fun Collection<PlaytimeSession>.sumByCategory(category: String): Long = this
    .filter { it.category == category }
    .sumOf { it.durationSeconds }
