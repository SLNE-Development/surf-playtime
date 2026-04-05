package dev.slne.surf.playtime.core.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.rabbitmq.api.RabbitMQApi

private val instance = requiredService<PlaytimeInstance>()

interface PlaytimeInstance {
    val rabbitApi: RabbitMQApi

    companion object : PlaytimeInstance by instance {
        val INSTANCE get() = instance
    }
}