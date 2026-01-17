package com.example.moneymanager.data.local.asset

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val assetId: Int = 0,
    val assetName: String,
    val assetType: String,
    val currentBalance: Double,
    val balanceUnit: String,
    val currencySymbol: String,
    val accountNumber: String? = null,
    val bankName: String? = null,
    val lastPriceUpdate: Double? = null,
    val priceSource: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdDate: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)
