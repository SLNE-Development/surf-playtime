package dev.slne.surf.playtime.api.common.session

import dev.slne.surf.api.core.serializer.java.datetime.date.local.SerializableLocalDate
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class PlaytimeStreak(
    val playerUuid: SerializableUUID,
    val currentLoginStreak: Int,
    val longestLoginStreak: Int,
    val lastLoginDate: SerializableLocalDate?
) {
    data class SimpleStreak(
        val currentLoginStreak: Int,
        val longestLoginStreak: Int
    )
}
