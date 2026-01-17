package com.example.moneymanager.data.local.transfer

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moneymanager.data.local.asset.AssetEntity

@Entity(
    tableName = "transfer_pairs",
    foreignKeys = [
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["assetId"],
            childColumns = ["fromAssetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AssetEntity::class,
            parentColumns = ["assetId"],
            childColumns = ["toAssetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fromAssetId"), Index("toAssetId")]
)
data class TransferPairEntity(
    @PrimaryKey(autoGenerate = true) val pairId: Int = 0,
    val fromAssetId: Int,
    val toAssetId: Int,
    val pairName: String,
    val lastUsed: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,
    val isQuickTransfer: Boolean = false,
    val createdDate: Long = System.currentTimeMillis()
)
