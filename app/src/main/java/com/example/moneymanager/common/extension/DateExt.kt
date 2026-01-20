package com.example.moneymanager.common.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toReadableDate(): String {
    val date = Date(this)
    val now = Date()
    val sdfCheck = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    return if (sdfCheck.format(date) == sdfCheck.format(now)) {
        "Hari Ini"
    } else {
        SimpleDateFormat(
            "dd MMM yyyy", 
            Locale.Builder().setLanguage("id").setRegion("ID").build()
        ).format(date)
    }
}