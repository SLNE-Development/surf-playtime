package dev.slne.surf.playtime.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.literalArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.longArgument
import dev.slne.minestom.lobby.api.command.commandapi.dsl.stringArgument
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.minestom.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.playtime.core.client.command.parseDate
import dev.slne.surf.playtime.core.client.command.sendConfigurationReloaded
import dev.slne.surf.playtime.core.client.command.sendEndDateBeforeStartDate
import dev.slne.surf.playtime.core.client.command.sendInvalidDate
import dev.slne.surf.playtime.core.client.command.sendNoSessionsFound
import dev.slne.surf.playtime.core.client.command.sendNoStreakPauses
import dev.slne.surf.playtime.core.client.command.sendPlayerNotFound
import dev.slne.surf.playtime.core.client.command.sendRecalculateAllFinished
import dev.slne.surf.playtime.core.client.command.sendRecalculateAllStarted
import dev.slne.surf.playtime.core.client.command.sendStreakPauseCreated
import dev.slne.surf.playtime.core.client.command.sendStreakPauseDeleted
import dev.slne.surf.playtime.core.client.command.sendStreakPauseList
import dev.slne.surf.playtime.core.client.command.sendStreakPauseNotFound
import dev.slne.surf.playtime.core.client.command.sendStreakRecalculated
import dev.slne.surf.playtime.core.client.config.playtimeConfigManager
import dev.slne.surf.playtime.core.client.permission.PlaytimePermissions
import dev.slne.surf.playtime.core.common.service.PlaytimeStreakService
import kotlinx.coroutines.Deferred

fun playtimeAdminCommand() = commandTree("playtimeadmin") {
    withPermission(PlaytimePermissions.COMMAND_ADMIN)
    literalArgument("reload") {
        anyExecutorSuspend { sender, _ ->
            playtimeConfigManager.reload()

            sender.sendConfigurationReloaded()
        }
    }

    literalArgument("streak") {
        literalArgument("recalculateall") {
            anyExecutorSuspend { sender, _ ->
                sender.sendRecalculateAllStarted()

                val count = PlaytimeStreakService.recalculateAllPlaytimeStreaks()

                sender.sendRecalculateAllFinished(count)
            }
        }

        literalArgument("recalculate") {
            surfOfflinePlayerArgument("player") {
                anyExecutorSuspend { sender, args ->
                    val player: Deferred<SurfPlayer?> by args

                    val targetPlayer = player.await()
                    if (targetPlayer == null) {
                        sender.sendPlayerNotFound()
                        return@anyExecutorSuspend
                    }

                    val streak =
                        PlaytimeStreakService.recalculatePlaytimeStreak(targetPlayer.uuid)

                    if (streak == null) {
                        sender.sendNoSessionsFound(targetPlayer.lastKnownName ?: "Unbekannt")
                        return@anyExecutorSuspend
                    }

                    PlaytimeStreakService.invalidateCache(targetPlayer.uuid)

                    sender.sendStreakRecalculated(
                        playerName = targetPlayer.lastKnownName ?: "Unbekannt",
                        currentLoginStreak = streak.currentLoginStreak,
                        longestLoginStreak = streak.longestLoginStreak
                    )
                }
            }
        }
    }

    literalArgument("streakpause") {
        literalArgument("create") {
            stringArgument("von") {
                stringArgument("bis") {
                    anyExecutorSuspend { sender, args ->
                        val von: String by args
                        val bis: String by args

                        val startDate = parseDate(von)
                        val endDate = parseDate(bis)

                        if (startDate == null || endDate == null) {
                            sender.sendInvalidDate()
                            return@anyExecutorSuspend
                        }

                        if (endDate.isBefore(startDate)) {
                            sender.sendEndDateBeforeStartDate()
                            return@anyExecutorSuspend
                        }

                        val pause = PlaytimeStreakService.createStreakPause(startDate, endDate)

                        sender.sendStreakPauseCreated(pause)
                    }
                }
            }
        }

        literalArgument("delete") {
            longArgument("id") {
                anyExecutorSuspend { sender, args ->
                    val id: Long by args

                    val deleted = PlaytimeStreakService.deleteStreakPause(id)

                    if (deleted) {
                        sender.sendStreakPauseDeleted(id)
                    } else {
                        sender.sendStreakPauseNotFound(id)
                    }
                }
            }
        }

        literalArgument("list") {
            anyExecutorSuspend { sender, _ ->
                val pauses = PlaytimeStreakService.loadStreakPauses()

                if (pauses.isEmpty()) {
                    sender.sendNoStreakPauses()
                    return@anyExecutorSuspend
                }

                sender.sendStreakPauseList(pauses)
            }
        }
    }
}
