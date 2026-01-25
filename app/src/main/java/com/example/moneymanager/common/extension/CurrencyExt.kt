package com.example.moneymanager.common.extension

import java.text.NumberFormat
import java.util.Locale

fun Double.toRupiah(): String {
    return try {
        val localeID = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        numberFormat.format(this)
    } catch (e: Exception) {
        "Rp $this"
    }
}

fun String.cleanToDouble(): Double {
    val cleanString = this.filter { it.isDigit() }
    return cleanString.toDoubleOrNull() ?: 0.0
}

fun String.formatToThousandSeparator(): String {
    val cleanString = this.filter { it.isDigit() }
    if (cleanString.isEmpty()) return ""

    return try {
        val parsed = cleanString.toLong()
        val localeID = Locale.Builder().setLanguage("id").setRegion("ID").build()
        val formatter = NumberFormat.getNumberInstance(localeID)
        formatter.maximumFractionDigits = 0
        formatter.format(parsed)
    } catch (e: Exception) {
        cleanString
    }
}