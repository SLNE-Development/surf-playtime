package dev.slne.surf.playtime.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar

/**
 * Registers the playtime commands of this plugin.
 */
class PlaytimeCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        playtimeCommand()
        playtimeAdminCommand()
    }
}
