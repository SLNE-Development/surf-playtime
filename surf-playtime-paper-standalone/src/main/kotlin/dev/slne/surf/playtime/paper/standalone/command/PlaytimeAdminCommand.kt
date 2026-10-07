package dev.slne.surf.playtime.paper.standalone.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.playtime.paper.standalone.config.playtimeConfigManager

fun playtimeAdminCommand() = commandTree("playtimeadmin") {
    withPermission(PlaytimePermissions.COMMAND_ADMIN)

    literalArgument("reload") {
        anyExecutor { sender, _ ->
            playtimeConfigManager.reload()

            sender.sendConfigurationReloaded()
        }
    }
}
