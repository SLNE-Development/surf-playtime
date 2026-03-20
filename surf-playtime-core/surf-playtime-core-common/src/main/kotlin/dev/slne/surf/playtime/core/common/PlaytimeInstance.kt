package dev.slne.surf.playtime.core.common

import dev.slne.surf.rabbitmq.api.RabbitMQApi
import dev.slne.surf.surfapi.core.api.util.requiredService

private val instance = requiredService<PlaytimeInstance>()

interface PlaytimeInstance {
    val rabbitApi: RabbitMQApi

    companion object : PlaytimeInstance by instance {
        val INSTANCE get() = instance
    }
}