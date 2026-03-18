package dev.slne.surf.playtime.fallback.converter

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.playtime.fallback.table.OldPlaytimeSessionsTable
import kotlinx.coroutines.flow.firstOrNull
import java.util.logging.Logger

object PlaytimeSessionConverter {
    private val logger = Logger.getLogger(PlaytimeSessionConverter::class.java.name)

    suspend fun renameOldTableIfNeeded() {
        val hasOldFormat = try {
            suspendTransaction {
                exec("SELECT `start_time` FROM `playtime_sessions` LIMIT 1")
            }
            true
        } catch (e: Exception) {
            logger.fine("playtime_sessions does not have old format, no rename needed: ${e.message}")
            false
        }

        if (hasOldFormat) {
            logger.info("Old playtime_sessions format detected. Renaming to playtime_sessions_old for migration.")
            suspendTransaction {
                exec("RENAME TABLE `playtime_sessions` TO `playtime_sessions_old`")
            }
        }
    }

    suspend fun migrateData() {
        val oldTableExists = try {
            suspendTransaction {
                OldPlaytimeSessionsTable.selectAll().limit(1).firstOrNull()
            }
            true
        } catch (e: Exception) {
            logger.fine("playtime_sessions_old does not exist, no migration needed: ${e.message}")
            false
        }

        if (!oldTableExists) return

        logger.info("Migrating playtime sessions from old format (start/end time) to new format (seconds).")

        suspendTransaction {
            exec(
                """
                INSERT INTO `playtime_sessions` (`session_uuid`, `playtime_player_id`, `server_name`, `server_category`, `seconds`)
                SELECT
                    old.`session_uuid`,
                    old.`playtime_player_id`,
                    old.`server_name`,
                    old.`server_category`,
                    TIMESTAMPDIFF(SECOND, old.`start_time`, old.`end_time`)
                FROM `playtime_sessions_old` old
                LEFT JOIN `playtime_sessions` new ON old.`session_uuid` = new.`session_uuid`
                WHERE new.`session_uuid` IS NULL
                  AND old.`end_time` > old.`start_time`
                """.trimIndent()
            )
        }

        logger.info("Playtime session migration completed.")
    }
}
