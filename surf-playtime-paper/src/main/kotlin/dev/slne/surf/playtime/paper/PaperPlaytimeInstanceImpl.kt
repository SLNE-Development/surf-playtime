package dev.slne.surf.playtime.paper

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.paper.PaperLoader
import dev.slne.surf.playtime.core.paper.PaperPlaytimeInstance

@AutoService(PaperPlaytimeInstance::class)
class PaperPlaytimeInstanceImpl : PaperPlaytimeInstance {
    override val paperLoader = PaperLoader(plugin.dataPath)
}