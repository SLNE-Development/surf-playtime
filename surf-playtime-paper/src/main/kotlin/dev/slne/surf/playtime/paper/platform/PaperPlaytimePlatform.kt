package dev.slne.surf.playtime.paper.platform

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.client.platform.PlaytimePlatform
import dev.slne.surf.playtime.paper.plugin
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(PlaytimePlatform::class)
class PaperPlaytimePlatform : PlaytimePlatform, Services.Fallback {
    override fun onlinePlayer(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)

    override fun launchAsync(block: suspend CoroutineScope.() -> Unit) = plugin.launch(block = block)
}
