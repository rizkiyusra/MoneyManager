package com.example.moneymanager.domain.model

import androidx.annotation.Keep

@Keep
data class BackupData(
    val version: Int = 1,
    val exportDate: Long = System.currentTimeMillis(),
    val assets: List<Asset>,
    val categories: List<Category>,
    val transactions: List<Transaction>
)