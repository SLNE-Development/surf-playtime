package dev.slne.surf.playtime.paper.hook

import dev.slne.surf.playtime.paper.playtimeConfig
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.transaction.api.currency.Currency
import dev.slne.surf.transaction.api.user.TransactionUser
import org.bukkit.entity.Player

object TransactionHook {
    suspend fun givePaycheck(player: Player) {
        val transactionUser = TransactionUser[player.uniqueId]
        val balance = transactionUser.balance(Currency.default())

        if (balance >= playtimeConfig.paycheck.maxBalance.toBigDecimal()) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst keine weiteren PayChecks erhalten, weil du bereits mehr als ")
                variableValue(playtimeConfig.paycheck.maxBalance.toString())
                append(Currency.default().symbolDisplay)
                error(" hast!")
            }
            return
        }

        val result = transactionUser.deposit(
            playtimeConfig.paycheck.amount.toBigDecimal(),
            Currency.default()
        )

        if (!result.success) {
            player.sendText {
                appendErrorPrefix()
                error("Bei der Auszahlung deines PayChecks ist ein Fehler aufgetreten: $result, sollte dieser Fehler weiterhin auftreten, wende dich bitte an den Support.")
            }
            return
        }

        player.sendText {
            appendInfoPrefix()
            info("Du hast einen PayCheck von ")
            variableValue(playtimeConfig.paycheck.amount)
            append(Currency.default().symbolDisplay)
            info(" erhalten!")
        }
    }
}