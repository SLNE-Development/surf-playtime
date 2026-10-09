package dev.slne.surf.playtime.paper.standalone.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class StandalonePlaytimeConfig(
    val serverName: String = "server",
    val serverCategory: String = "default",
    val afkTimeoutSeconds: Long = 180,
)
