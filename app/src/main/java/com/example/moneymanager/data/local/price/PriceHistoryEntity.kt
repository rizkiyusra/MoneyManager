package com.example.moneymanager.data.local.price

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_history")
data class PriceHistoryEntity(
    @PrimaryKey(autoGenerate = true) val priceId: Int = 0,
    val currencyCode: String,
    val priceInIDR: Double,
    val priceInUSD: Double,
    val priceSource: String,
    val priceDate: Long,
    val isLatest: Boolean = false
)
