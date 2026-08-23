package dev.slne.surf.playtime.microservice.repository

import dev.slne.surf.api.core.util.mutableObject2IntMapOf
import dev.slne.surf.api.core.util.mutableObject2ObjectMapOf
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.playtime.api.common.session.PlaytimeStreak
import dev.slne.surf.playtime.microservice.table.PlaytimeSessionsTable
import dev.slne.surf.playtime.microservice.table.PlaytimeStreakPausesTable
import dev.slne.surf.playtime.microservice.table.PlaytimeStreaksTable
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
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
        val currentHighest = loadHighestStreak(playerUuid)
        val newHighest = maxOf(currentHighest, streak)

        PlaytimeStreaksTable.upsert(PlaytimeStreaksTable.playerUuid) {
            it[this.playerUuid] = playerUuid
            it[currentLoginStreak] = streak
            it[highestLoginStreak] = newHighest
            it[lastLoginDate] = date
        }
    }

    suspend fun calculateStreak(playerUuid: UUID) = suspendTransaction {
        val loginDates = loadLoginDates(playerUuid)

        if (loginDates.isEmpty()) {
            return@suspendTransaction 0
        }

        countStreak(loginDates, loadPauseRanges(), LocalDate.now())
    }

    /**
     * Recalculates the streak from the stored sessions (pause-aware) as of the player's
     * last login date and persists the result. Returns null if the player has no sessions.
     */
    suspend fun recalculateStreak(playerUuid: UUID) = suspendTransaction {
        val loginDates = loadLoginDates(playerUuid)

        if (loginDates.isEmpty()) {
            return@suspendTransaction null
        }

        val lastLogin = loginDates.max()
        val streak = countStreak(loginDates, loadPauseRanges(), lastLogin)

        val newHighest = maxOf(loadHighestStreak(playerUuid), streak)

        PlaytimeStreaksTable.upsert(PlaytimeStreaksTable.playerUuid) {
            it[this.playerUuid] = playerUuid
            it[currentLoginStreak] = streak
            it[highestLoginStreak] = newHighest
            it[lastLoginDate] = lastLogin
        }

        PlaytimeStreak(playerUuid, streak, newHighest, lastLogin)
    }

    /**
     * Recalculates and persists the streaks of every player that has at least one
     * stored session. Returns the number of recalculated players.
     */
    suspend fun recalculateAllStreaks() = suspendTransaction {
        val loginDatesByPlayer = mutableObject2ObjectMapOf<UUID, ObjectOpenHashSet<LocalDate>>()

        PlaytimeSessionsTable
            .select(PlaytimeSessionsTable.playerUuid, PlaytimeSessionsTable.startTime)
            .collect { row ->
                loginDatesByPlayer
                    .getOrPut(row[PlaytimeSessionsTable.playerUuid]) { ObjectOpenHashSet() }
                    .add(row[PlaytimeSessionsTable.startTime].toLocalDate())
            }

        if (loginDatesByPlayer.isEmpty()) {
            return@suspendTransaction 0
        }

        val pauses = loadPauseRanges()

        val highestByPlayer = mutableObject2IntMapOf<UUID>().apply { defaultReturnValue(0) }

        PlaytimeStreaksTable
            .select(PlaytimeStreaksTable.playerUuid, PlaytimeStreaksTable.highestLoginStreak)
            .collect { row ->
                highestByPlayer.put(
                    row[PlaytimeStreaksTable.playerUuid],
                    row[PlaytimeStreaksTable.highestLoginStreak]
                )
            }

        for ((uuid, loginDates) in loginDatesByPlayer) {
            val lastLogin = loginDates.max()
            val streak = countStreak(loginDates, pauses, lastLogin)
            val newHighest = maxOf(highestByPlayer.getInt(uuid), streak)

            PlaytimeStreaksTable.upsert(PlaytimeStreaksTable.playerUuid) {
                it[this.playerUuid] = uuid
                it[currentLoginStreak] = streak
                it[highestLoginStreak] = newHighest
                it[lastLoginDate] = lastLogin
            }
        }

        loginDatesByPlayer.size
    }

    private suspend fun loadHighestStreak(playerUuid: UUID) = PlaytimeStreaksTable
        .select(PlaytimeStreaksTable.highestLoginStreak)
        .where { PlaytimeStreaksTable.playerUuid eq playerUuid }
        .firstOrNull()
        ?.get(PlaytimeStreaksTable.highestLoginStreak)
        ?: 0

    private suspend fun loadLoginDates(playerUuid: UUID): Set<LocalDate> = PlaytimeSessionsTable
        .select(PlaytimeSessionsTable.startTime)
        .where { PlaytimeSessionsTable.playerUuid eq playerUuid }
        .map { it[PlaytimeSessionsTable.startTime].toLocalDate() }
        .toCollection(mutableObjectSetOf())

    private suspend fun loadPauseRanges() = PlaytimeStreakPausesTable.selectAll()
        .map { it[PlaytimeStreakPausesTable.startDate]..it[PlaytimeStreakPausesTable.endDate] }
        .toList()

    private fun countStreak(
        loginDates: Set<LocalDate>,
        pauses: List<ClosedRange<LocalDate>>,
        from: LocalDate
    ): Int {
        val earliestLogin = loginDates.min()
        var streak = 0
        var expected = from

        while (!expected.isBefore(earliestLogin)) {
            when {
                expected in loginDates -> {
                    streak++
                    expected = expected.minusDays(1)
                }

                // paused days without a login neither break nor count toward the streak
                pauses.any { expected in it } -> expected = expected.minusDays(1)

                else -> break
            }
        }

        return streak
    }
}
