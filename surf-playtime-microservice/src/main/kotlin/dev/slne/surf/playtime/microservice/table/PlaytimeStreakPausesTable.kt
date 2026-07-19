package dev.slne.surf.playtime.microservice.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.date

object PlaytimeStreakPausesTable : LongIdTable("playtime_streak_pauses") {
    val startDate = date("start_date")
    val endDate = date("end_date")
}
