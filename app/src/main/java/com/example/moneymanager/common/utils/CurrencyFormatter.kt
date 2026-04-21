package com.example.moneymanager.common.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    /**
     *
     * @param amount
     * @param currencyCode
     */

    fun format(amount: Double, currencyCode: String): String {
        return when (currencyCode.uppercase(Locale.ROOT)) {
            "IDR" -> {
                val symbols = DecimalFormatSymbols(Locale.Builder().setLanguage("id").setRegion("ID").build())
                val format = DecimalFormat("Rp #,###", symbols)
                format.format(amount)
            }
            "USD" -> {
                val symbols = DecimalFormatSymbols(Locale.US)
                val format = DecimalFormat("$ #,###.00", symbols)
                format.format(amount)
            }
            "BTC" -> {
                val symbols = DecimalFormatSymbols(Locale.US)
                val format = DecimalFormat("₿ #,##0.00000000", symbols)
                format.format(amount)
            }
            "XAU", "GOLD" -> {
                val symbols = DecimalFormatSymbols(Locale.Builder().setLanguage("id").setRegion("ID").build())
                val format = DecimalFormat("#,##0.00", symbols)
                "${format.format(amount)} gram"
            }
            else -> {
                val symbols = DecimalFormatSymbols(Locale.getDefault())
                val format = DecimalFormat("#,###.##", symbols)
                "${currencyCode.uppercase(Locale.ROOT)} ${format.format(amount)}"
            }
        }
    }
}