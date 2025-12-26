package dev.slne.surf.playtime.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

val databaseBridge = requiredService<DatabaseBridge>()

interface DatabaseBridge {
    fun initialize(path: Path)
    fun disconnect()
}