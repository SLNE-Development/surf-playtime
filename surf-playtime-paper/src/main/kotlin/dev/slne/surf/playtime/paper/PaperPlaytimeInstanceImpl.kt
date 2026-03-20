package dev.slne.surf.playtime.paper

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.common.PlaytimeInstance
import dev.slne.surf.playtime.core.paper.PaperLoader
import dev.slne.surf.playtime.core.paper.PaperPlaytimeInstance
import net.kyori.adventure.util.Services

@AutoService(PlaytimeInstance::class)
class PaperPlaytimeInstanceImpl : PaperPlaytimeInstance, Services.Fallback {
    override val paperLoader = PaperLoader(plugin.dataPath)
}