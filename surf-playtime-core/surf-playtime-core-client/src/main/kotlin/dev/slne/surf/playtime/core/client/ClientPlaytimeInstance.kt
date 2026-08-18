package dev.slne.surf.playtime.core.client

import dev.slne.surf.playtime.core.common.PlaytimeInstance
import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

/**
 * The [PlaytimeInstance] of a game server that talks to the playtime microservice as a client.
 */
interface ClientPlaytimeInstance : PlaytimeInstance {
    /**
     * The directory this plugin stores its files in.
     */
    val dataPath: Path

    val clientLoader: ClientLoader

    override val rabbitApi: ClientRabbitMQApi get() = clientLoader.rabbitApi

    companion object :
        ClientPlaytimeInstance by PlaytimeInstance.INSTANCE as ClientPlaytimeInstance {
        val INSTANCE get() = PlaytimeInstance.INSTANCE
    }
}

/**
 * Base class for [ClientPlaytimeInstance] implementations that derives the loader from
 * [dataPath].
 */
abstract class AbstractClientPlaytimeInstance : ClientPlaytimeInstance {
    override val clientLoader: ClientLoader by lazy { ClientLoader(dataPath) }
}
