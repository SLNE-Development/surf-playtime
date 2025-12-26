package dev.slne.surf.playtime.paper.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class PlaytimeConfig(
    val serverName: String,
    val serverCategory: String
)
