package dev.slne.surf.playtime.api.common.session

import dev.slne.surf.api.core.serializer.java.datetime.date.local.SerializableLocalDate
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class PlaytimeStreakPause(
    val id: Long,
    val startDate: SerializableLocalDate,
    val endDate: SerializableLocalDate
) {
    operator fun contains(date: LocalDate) =
        !date.isBefore(startDate) && !date.isAfter(endDate)
}
