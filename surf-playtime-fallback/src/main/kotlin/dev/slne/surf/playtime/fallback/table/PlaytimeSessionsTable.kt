package dev.slne.surf.playtime.fallback.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.datetime

object PlaytimeSessionsTable : LongIdTable("playtime_sessions") {
    val sessionUuid = uuid("session_uuid").uniqueIndex()
    val playerUuid = uuid("playtime_player_id")
    val serverName = varchar("server_name", 255)
    val serverCategory = varchar("server_category", 255)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
}