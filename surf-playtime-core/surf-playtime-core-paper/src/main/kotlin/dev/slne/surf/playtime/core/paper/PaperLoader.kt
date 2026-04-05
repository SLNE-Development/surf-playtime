package dev.slne.surf.playtime.core.paper

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

class PaperLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-playtime", dataPath)

    suspend fun onLoad() {
        rabbitApi.freezeAndConnect()
    }

    suspend fun onEnable() {
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }
}