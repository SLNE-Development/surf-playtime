package dev.slne.surf.playtime.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.playtime.api.common.session.PlaytimeStreakPause
import dev.slne.surf.playtime.microservice.table.PlaytimeStreakPausesTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.time.LocalDate

object PlaytimeStreakPauseRepository {
    suspend fun findAllPauses() = suspendTransaction {
        PlaytimeStreakPausesTable.selectAll()
            .orderBy(PlaytimeStreakPausesTable.startDate)
            .map { row ->
                PlaytimeStreakPause(
                    id = row[PlaytimeStreakPausesTable.id].value,
                    startDate = row[PlaytimeStreakPausesTable.startDate],
                    endDate = row[PlaytimeStreakPausesTable.endDate]
                )
            }.toList()
    }

    suspend fun createPause(startDate: LocalDate, endDate: LocalDate) = suspendTransaction {
        val id = PlaytimeStreakPausesTable.insertAndGetId {
            it[this.startDate] = startDate
            it[this.endDate] = endDate
        }

        PlaytimeStreakPause(id.value, startDate, endDate)
    }

    suspend fun deletePause(pauseId: Long) = suspendTransaction {
        PlaytimeStreakPausesTable.deleteWhere { PlaytimeStreakPausesTable.id eq pauseId } > 0
    }
}
