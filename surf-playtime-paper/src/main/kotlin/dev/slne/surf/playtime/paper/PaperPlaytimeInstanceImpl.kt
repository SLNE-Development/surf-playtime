package dev.slne.surf.playtime.paper

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.client.AbstractClientPlaytimeInstance
import dev.slne.surf.playtime.core.common.PlaytimeInstance
import net.kyori.adventure.util.Services

@AutoService(PlaytimeInstance::class)
class PaperPlaytimeInstanceImpl : AbstractClientPlaytimeInstance(), Services.Fallback {
    override val dataPath get() = plugin.dataPath
}
