package dev.slne.surf.playtime.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.javatime.datetime

object PlaytimeSessionsTable : LongIdTable("playtime_sessions") {
    val sessionUuid = nativeUuid("session_uuid").uniqueIndex()
    val playerUuid = nativeUuid("playtime_player_id")
    val serverName = varchar("server_name", 255)
    val serverCategory = varchar("server_category", 255)
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")

    init {
        index("idx_playtime_sessions_player_server", false, playerUuid, serverName)
        index("idx_playtime_sessions_player_category", false, playerUuid, serverCategory)
    }
}
