package dev.slne.surf.playtime.fallback.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.datetime

object OldPlaytimeSessionsTable : Table("playtime_sessions_old") {
    val id = long("id").autoIncrement()
    val sessionUuid = nativeUuid("session_uuid")
    val playerUuid = nativeUuid("playtime_player_id")
    val serverName = varchar("server_name", 255)
    val serverCategory = varchar("server_category", 255)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    override val primaryKey = PrimaryKey(id)
}
