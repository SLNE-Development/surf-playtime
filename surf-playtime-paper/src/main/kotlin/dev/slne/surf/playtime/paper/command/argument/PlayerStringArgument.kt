package dev.slne.surf.playtime.paper.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.Bukkit

class PlayerStringArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        info.input
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            Bukkit.getOnlinePlayers().map { it.name }
        })
    }
}

inline fun CommandTree.playerStringArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    PlayerStringArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.playerStringArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    PlayerStringArgument(nodeName).setOptional(optional).apply(block)
)