package com.kevinfreyap.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

const val CURRENCY_SYMBOL_PHP = "₱"

val phpLocale: Locale = Locale.Builder()
    .setLanguage("en")
    .setRegion("PH")
    .build()

private val phpDecimalSymbols: DecimalFormatSymbols = DecimalFormatSymbols(phpLocale).apply {
    currencySymbol = CURRENCY_SYMBOL_PHP
}

fun Double?.toFormattedCurrency(): String {
    if (this == null) return "${CURRENCY_SYMBOL_PHP}0.00"
    val formatter = (NumberFormat.getCurrencyInstance(phpLocale) as DecimalFormat).apply {
        decimalFormatSymbols = phpDecimalSymbols
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(this)
}

fun Float?.toFormattedCurrency(): String = this?.toDouble().toFormattedCurrency()

fun Int?.toFormattedCurrency(): String = this?.toDouble().toFormattedCurrency()

fun String?.parseCurrencyAmount(): Double {
    if (this.isNullOrBlank()) return 0.0
    val cleaned = this
        .replace(CURRENCY_SYMBOL_PHP, "")
        .replace("PHP", "", ignoreCase = true)
        .replace(",", "")
        .trim()
    return cleaned.toDoubleOrNull() ?: 0.0
}

fun Int.toFormattedNumber(): String {
    val formatter = NumberFormat.getNumberInstance(phpLocale)
    return formatter.format(this)
}

fun formatStockDisplay(quantity: Int, unit: String = "pcs"): String {
    val cleanUnit = unit.ifBlank { "pcs" }
    return "$quantity $cleanUnit"
}
