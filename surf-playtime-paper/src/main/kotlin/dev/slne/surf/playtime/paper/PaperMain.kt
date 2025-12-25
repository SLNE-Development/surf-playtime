package dev.slne.surf.playtime.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.playtime.paper.listener.PlayerAfkListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        PlayerAfkListener.register()
        PlayerAfkListener.afkCheckTask()
    }
}