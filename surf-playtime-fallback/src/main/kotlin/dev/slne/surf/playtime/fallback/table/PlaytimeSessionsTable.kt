package dev.slne.surf.playtime.fallback.table

import org.jetbrains.exposed.dao.id.LongIdTable

object PlaytimeSessionsTable : LongIdTable("playtime_sessions") {
    val sessionUuid = uuid("session_uuid").uniqueIndex()
    val playerId = long("playtime_player_id").references(PlaytimePlayerTable.id)
    val serverName = varchar("server_name", 255)
    val category = varchar("category", 255)
    val durationSeconds = long("duration_seconds").default(0)
}