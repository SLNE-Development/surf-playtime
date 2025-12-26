package dev.slne.surf.playtime.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.database.database.DatabaseProvider
import dev.slne.surf.playtime.core.DatabaseBridge
import dev.slne.surf.playtime.fallback.table.PlaytimePlayerTable
import dev.slne.surf.playtime.fallback.table.PlaytimeSessionsTable
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path

@AutoService(DatabaseBridge::class)
class DatabaseBridgeImpl : DatabaseBridge, Services.Fallback {
    lateinit var databaseProvider: DatabaseProvider
    override fun initialize(path: Path) {
        databaseProvider = DatabaseManager(path, path).databaseProvider
        databaseProvider.connect()

        transaction {
            SchemaUtils.create(PlaytimePlayerTable, PlaytimeSessionsTable)
        }
    }

    override fun disconnect() {
        if (::databaseProvider.isInitialized) {
            databaseProvider.disconnect()
        }
    }
}