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