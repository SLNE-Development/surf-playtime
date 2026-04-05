package dev.slne.surf.playtime.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.paper.playtimeConfigManager

fun playtimeAdminCommand() = commandTree("playtimeadmin") {
    withPermission("surf.playtime.command.admin")
    literalArgument("reload") {
        anyExecutor { sender, _ ->
            playtimeConfigManager.reload()

            sender.sendText {
                appendSuccessPrefix()
                success("Die Konfiguration wurde neu geladen.")
            }
        }
    }
}