package dev.slne.surf.playtime.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

val castCoinFormat = DecimalFormat("#,##0.## ¤", DecimalFormatSymbols(Locale.GERMANY).apply {
    decimalSeparator = ','
    groupingSeparator = '.'
    currencySymbol = "CC"
})