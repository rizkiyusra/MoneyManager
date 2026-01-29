package com.example.moneymanager.data.local.recurring

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val note: String,
    val isIncome: Boolean,
    val categoryId: Int,
    val assetId: Int,
    val frequency: String,
    val nextRunDate: Long,
    val createdDate: Long = System.currentTimeMillis(),
)