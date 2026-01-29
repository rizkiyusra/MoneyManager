package com.example.moneymanager.domain.model

data class RecurringTransaction(
    val id: Int = 0,
    val amount: Double,
    val note: String,
    val isIncome: Boolean,
    val categoryId: Int,
    val assetId: Int,
    val frequency: String,
    val nextRunDate: Long,
    val createdDate: Long
)