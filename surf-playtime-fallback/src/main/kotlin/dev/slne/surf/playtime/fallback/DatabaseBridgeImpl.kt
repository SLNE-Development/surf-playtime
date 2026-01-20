package dev.slne.surf.playtime.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.playtime.core.DatabaseBridge
import dev.slne.surf.playtime.fallback.table.PlaytimeSessionsTable
import net.kyori.adventure.util.Services
import java.nio.file.Path

@AutoService(DatabaseBridge::class)
class DatabaseBridgeImpl : DatabaseBridge, Services.Fallback {
    lateinit var databaseApi: DatabaseApi
    override suspend fun initialize(path: Path) {
        databaseApi = DatabaseApi.create(path)

        suspendTransaction {
            SchemaUtils.create(PlaytimeSessionsTable)
        }
    }

    override fun disconnect() {
        if (::databaseApi.isInitialized) {
            databaseApi.shutdown()
        }
    }
}