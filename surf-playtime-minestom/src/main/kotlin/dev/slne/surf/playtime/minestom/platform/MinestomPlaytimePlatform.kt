package dev.slne.surf.playtime.minestom.platform

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.playtime.core.client.platform.PlaytimePlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@AutoService(PlaytimePlatform::class)
class MinestomPlaytimePlatform : PlaytimePlatform {
    override fun onlinePlayer(playerUuid: UUID) = ConnectionManager.getOnlinePlayerByUuid(playerUuid)

    override fun launchAsync(block: suspend CoroutineScope.() -> Unit) =
        minestomAsyncScope.launch(block = block)
}
