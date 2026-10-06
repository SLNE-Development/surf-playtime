package dev.slne.surf.playtime.core.client.paycheck

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.playtime.core.client.config.playtimeConfig
import dev.slne.surf.transaction.api.currency.Currency
import dev.slne.surf.transaction.api.user.TransactionUser
import net.kyori.adventure.audience.Audience
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PayCheckPayout {
    suspend fun give(playerUuid: UUID, audience: Audience) {
        val transactionUser = TransactionUser[playerUuid]
        val balance = transactionUser.balance(Currency.default())

        if (balance >= playtimeConfig.paycheck.maxBalance.toBigDecimal()) {
            audience.sendText {
                appendErrorPrefix()
                error("Du kannst keine weiteren PayChecks erhalten, weil du bereits mehr als ")
                variableValue(
                    formatCurrency(
                        playtimeConfig.paycheck.amount.toBigDecimal(),
                        Currency.default()
                    )
                )
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
            variableValue(
                formatCurrency(
                    playtimeConfig.paycheck.amount.toBigDecimal(),
                    Currency.default()
                )
            )
            info(" erhalten!")
        }
    }

    suspend fun giveEventCoinPayout(playerUuid: UUID, audience: Audience) {
        val transactionUser = TransactionUser[playerUuid]
        val currency = Currency.byName(playtimeConfig.eventCoinPayout.currencyName)
            ?: error("Event Coin Currency ${playtimeConfig.eventCoinPayout.currencyName} not found!")
        val balance = transactionUser.balance(currency)
        val maxBalance = playtimeConfig.eventCoinPayout.maxBalance?.toBigDecimal()

        if (maxBalance != null && balance >= maxBalance) {
            audience.sendText {
                appendErrorPrefix()
                error("Du kannst keine weiteren Event Paychecks erhalten, weil du bereits mehr als ")
                variableValue(
                    formatCurrency(
                        playtimeConfig.eventCoinPayout.amount.toBigDecimal(),
                        currency
                    )
                )
                error(" hast!")
            }
            return
        }

        val result = transactionUser.deposit(
            playtimeConfig.eventCoinPayout.amount.toBigDecimal(),
            currency
        )

        if (!result.success) {
            audience.sendText {
                appendErrorPrefix()
                error("Bei der Auszahlung deines Event Coin Payouts ist ein Fehler aufgetreten: $result. Bitte wende dich an den Support.")
            }
            return
        }

        audience.sendText {
            appendInfoPrefix()
            info("Du hast einen Event Paycheck von ")
            variableValue(
                formatCurrency(
                    playtimeConfig.eventCoinPayout.amount.toBigDecimal(),
                    currency
                )
            )
            info(" erhalten!")
        }
    }

    private val currencyFormats = ConcurrentHashMap<String, DecimalFormat>()

    private fun formatCurrency(amount: BigDecimal, currency: Currency): String {
        val formatter = currencyFormats.computeIfAbsent(currency.symbol) {
            DecimalFormat(
                "#,##0.## ¤",
                DecimalFormatSymbols(Locale.GERMANY).apply {
                    decimalSeparator = ','
                    groupingSeparator = '.'
                    currencySymbol = it
                }
            )
        }

        return synchronized(formatter) {
            formatter.format(amount)
        }
    }
}
