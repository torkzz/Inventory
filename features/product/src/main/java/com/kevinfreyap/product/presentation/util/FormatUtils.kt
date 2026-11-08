package com.kevinfreyap.product.presentation.util

import com.kevinfreyap.ui.util.toFormattedCurrency as centralizedToFormattedCurrency
import com.kevinfreyap.ui.util.toFormattedNumber as centralizedToFormattedNumber
import com.kevinfreyap.ui.util.phpLocale
import java.util.Locale

val locale: Locale = phpLocale

fun Int.toFormattedNumber(): String = this.centralizedToFormattedNumber()

fun Double.toFormattedCurrency(): String = (this as Double?).centralizedToFormattedCurrency()

fun Float.toFormattedCurrency(): String = (this.toDouble() as Double?).centralizedToFormattedCurrency()
