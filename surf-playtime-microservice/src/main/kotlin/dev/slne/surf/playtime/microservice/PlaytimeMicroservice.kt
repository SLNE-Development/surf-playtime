package dev.slne.surf.playtime.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.playtime.microservice.handler.PlaytimeSessionHandler
import dev.slne.surf.playtime.microservice.table.PlaytimeSessionsTable
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import kotlin.io.path.Path

@AutoService(Microservice::class)
class PlaytimeMicroservice : Microservice() {
    private val databaseApi = DatabaseApi.create(Path("config"))
    private val rabbitApi = ServerRabbitMQApi.create(1, "surf-playtime")

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                PlaytimeSessionsTable
            )
        }

        rabbitApi.registerRequestHandler(PlaytimeSessionHandler)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}