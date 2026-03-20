package dev.slne.surf.playtime.core.paper

import dev.slne.surf.playtime.core.common.PlaytimeInstance
import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi

interface PaperPlaytimeInstance : PlaytimeInstance {
    val paperLoader: PaperLoader

    override val rabbitApi: ClientRabbitMQApi get() = paperLoader.rabbitApi

    companion object : PaperPlaytimeInstance by PlaytimeInstance.INSTANCE as PaperPlaytimeInstance {
        val INSTANCE get() = PlaytimeInstance.INSTANCE
    }
}