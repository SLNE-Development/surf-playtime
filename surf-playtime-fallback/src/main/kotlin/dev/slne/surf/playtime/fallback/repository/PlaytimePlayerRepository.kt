package dev.slne.surf.playtime.fallback.repository

import dev.slne.surf.playtime.api.player.PlaytimePlayer
import dev.slne.surf.playtime.fallback.table.PlaytimePlayerTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.upsert
import java.util.*

val playtimePlayerRepository = PlaytimePlayerRepository()

class PlaytimePlayerRepository {
    suspend fun loadPlayerByUuid(uuid: UUID): PlaytimePlayer? =
        newSuspendedTransaction(Dispatchers.IO) {
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
        newSuspendedTransaction(Dispatchers.IO) {
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
        newSuspendedTransaction(Dispatchers.IO) {
            PlaytimePlayerTable.upsert(PlaytimePlayerTable.uuid) { row ->
                row[uuid] = playtimePlayer.uuid
                row[name] = playtimePlayer.name
            }
        }.let {
            Unit
        }
}