package dev.slne.surf.playtime.fallback.table

import org.jetbrains.exposed.dao.id.LongIdTable

object PlaytimePlayerTable : LongIdTable("playtime_players") {
    val uuid = uuid("uuid").uniqueIndex()
    val name = varchar("name", 16)
}