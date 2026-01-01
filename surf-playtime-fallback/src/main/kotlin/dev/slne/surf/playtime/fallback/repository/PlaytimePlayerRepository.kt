package dev.slne.surf.playtime.fallback.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.fallback.table.PlaytimePlayerTable
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

val playtimePlayerRepository = PlaytimePlayerRepository()

class PlaytimePlayerRepository {
    suspend fun loadPlayerByUuid(uuid: UUID): PlaytimePlayer? =
        suspendTransaction {
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.uuid eq uuid)
                .firstOrNull()
                ?.let { row ->
                    PlaytimePlayer(
                        name = row[PlaytimePlayerTable.name],
                        uuid = row[PlaytimePlayerTable.uuid]
                    )
                }
        }

    suspend fun loadPlayerByName(name: String): PlaytimePlayer? =
        suspendTransaction {
            PlaytimePlayerTable.selectAll().where(PlaytimePlayerTable.name eq name)
                .firstOrNull()
                ?.let { row ->
                    PlaytimePlayer(
                        name = row[PlaytimePlayerTable.name],
                        uuid = row[PlaytimePlayerTable.uuid]
                    )
                }
        }

    suspend fun savePlayer(playtimePlayer: PlaytimePlayer) =
        suspendTransaction {
            PlaytimePlayerTable.upsert { row ->
                row[uuid] = playtimePlayer.uuid
                row[name] = playtimePlayer.name
            }
        }.let {
            Unit
        }
}