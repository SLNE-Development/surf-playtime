package dev.slne.surf.playtime.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.microservice.table.PlaytimeSessionsTable
import dev.slne.surf.playtime.microservice.table.PlaytimeStreaksTable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.time.LocalDate
import java.util.*

object PlaytimeStreakRepository {
    suspend fun findStreakInformation(playerUuid: UUID) = suspendTransaction {
        PlaytimeStreaksTable.selectAll().where(PlaytimeStreaksTable.playerUuid eq playerUuid)
            .map { row ->
                PlaytimeStreak(
                    playerUuid = playerUuid,
                    currentLoginStreak = row[PlaytimeStreaksTable.currentLoginStreak],
                    longestLoginStreak = row[PlaytimeStreaksTable.highestLoginStreak],
                    lastLoginDate = row[PlaytimeStreaksTable.lastLoginDate]
                )
            }.firstOrNull()
    }


    suspend fun saveStreak(playerUuid: UUID, streak: Int, date: LocalDate) = suspendTransaction {
        val existing = PlaytimeStreaksTable
            .selectAll()
            .where { PlaytimeStreaksTable.playerUuid eq playerUuid }
            .firstOrNull()

        val currentHighest = existing?.get(PlaytimeStreaksTable.highestLoginStreak) ?: 0
        val newHighest = maxOf(currentHighest, streak)

        PlaytimeStreaksTable.upsert {
            it[this.playerUuid] = playerUuid
            it[currentLoginStreak] = streak
            it[highestLoginStreak] = newHighest
            it[lastLoginDate] = date
        }
    }

    suspend fun calculateStreak(playerUuid: UUID) = suspendTransaction {
        val loginDates = PlaytimeSessionsTable
            .select(PlaytimeSessionsTable.startTime)
            .where { PlaytimeSessionsTable.playerUuid eq playerUuid }
            .map { it[PlaytimeSessionsTable.startTime].toLocalDate() }
            .toSet()
            .sortedDescending()

        if (loginDates.isEmpty()) {
            return@suspendTransaction 0
        }

        var streak = 0
        var expected = LocalDate.now()

        for (date in loginDates) {
            if (date == expected) {
                streak++
                expected = expected.minusDays(1)
            } else if (date.isBefore(expected)) {
                break
            }
        }

        streak
    }
}