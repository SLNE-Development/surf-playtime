package dev.slne.surf.playtime.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.playtime.core.client.AbstractClientPlaytimeInstance
import dev.slne.surf.playtime.core.common.PlaytimeInstance

@AutoService(PlaytimeInstance::class)
class MinestomPlaytimeInstance : AbstractClientPlaytimeInstance() {
    override val dataPath get() = PlaytimeMinestomEntrypoint.dataPath
}
