package dev.slne.surf.playtime.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.date

object PlaytimeStreaksTable : LongIdTable("playtime_streaks") {
    val playerUuid = nativeUuid("player_uuid").uniqueIndex()

    val currentLoginStreak = integer("current_login_streak").default(0)
    val highestLoginStreak = integer("highest_login_streak").default(0)

    val lastLoginDate = date("last_login_date").nullable()
}