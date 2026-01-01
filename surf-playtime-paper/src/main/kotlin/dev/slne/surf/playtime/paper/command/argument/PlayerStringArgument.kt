package dev.slne.surf.playtime.paper.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.core.api.common.surfCoreApi

class PlayerStringArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        info.input
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            surfCoreApi.getOnlinePlayers().mapNotNull { it.lastKnownName }
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