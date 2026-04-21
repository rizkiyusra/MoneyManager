package com.example.moneymanager.common.extension

import com.example.moneymanager.common.utils.CurrencyFormatter
import java.text.NumberFormat
import java.util.Locale

fun Double.toRupiah(): String {
    return CurrencyFormatter.format(this, "IDR")
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
    } catch (_: Exception) {
        cleanString
    }
}