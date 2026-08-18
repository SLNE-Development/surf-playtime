package dev.slne.surf.playtime.core.client.paycheck

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.core.client.config.playtimeConfig
import dev.slne.surf.playtime.core.common.castCoinFormat
import dev.slne.surf.transaction.api.currency.Currency
import dev.slne.surf.transaction.api.user.TransactionUser
import net.kyori.adventure.audience.Audience
import java.util.*

/**
 * Pays out a single paycheck through the transaction api.
 */
object PayCheckPayout {
    suspend fun give(playerUuid: UUID, audience: Audience) {
        val transactionUser = TransactionUser[playerUuid]
        val balance = transactionUser.balance(Currency.default())

        if (balance >= playtimeConfig.paycheck.maxBalance.toBigDecimal()) {
            audience.sendText {
                appendErrorPrefix()
                error("Du kannst keine weiteren PayChecks erhalten, weil du bereits mehr als ")
                variableValue(castCoinFormat.format(playtimeConfig.paycheck.maxBalance))
                error(" hast!")
            }
            return
        }

        val result = transactionUser.deposit(
            playtimeConfig.paycheck.amount.toBigDecimal(),
            Currency.default()
        )

        if (!result.success) {
            audience.sendText {
                appendErrorPrefix()
                error("Bei der Auszahlung deines PayChecks ist ein Fehler aufgetreten: $result, sollte dieser Fehler weiterhin auftreten, wende dich bitte an den Support.")
            }
            return
        }

        audience.sendText {
            appendInfoPrefix()
            info("Du hast einen PayCheck von ")
            variableValue(castCoinFormat.format(playtimeConfig.paycheck.amount))
            info(" erhalten!")
        }
    }
}
