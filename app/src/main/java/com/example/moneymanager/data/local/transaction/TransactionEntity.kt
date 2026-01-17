package com.example.moneymanager.data.local.transaction

import androidx.room.*
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.data.local.category.CategoryEntity

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["assetId"],
            childColumns = ["fromAssetId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["assetId"],
            childColumns = ["toAssetId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("fromAssetId"), Index("toAssetId"), Index("categoryId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val transactionId: Int = 0,
    val fromAssetId: Int,
    val toAssetId: Int? = null,
    val categoryId: Int? = null,
    val transactionType: String,
    val transactionCategory: String,
    val transactionAmount: Double,
    val transactionCurrency: String,
    val convertedAmountIDR: Double,
    val exchangeRate: Double = 1.0,
    val transactionTitle: String,
    val transactionNote: String? = null,
    val transactionLocation: String? = null,
    val receiptImagePath: String? = null,
    val transactionDate: Long,
    val createdDate: Long = System.currentTimeMillis()
)

