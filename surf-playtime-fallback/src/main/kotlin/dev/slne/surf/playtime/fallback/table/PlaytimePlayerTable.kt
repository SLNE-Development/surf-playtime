package dev.slne.surf.playtime.fallback.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object PlaytimePlayerTable : LongIdTable("playtime_players") {
    val uuid = uuid("uuid").uniqueIndex()
    val name = varchar("name", 16)
}