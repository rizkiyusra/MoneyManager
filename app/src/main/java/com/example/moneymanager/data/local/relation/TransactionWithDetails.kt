package com.example.moneymanager.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.data.local.category.CategoryEntity
import com.example.moneymanager.data.local.transaction.TransactionEntity

data class TransactionWithDetails(
    @Embedded val transaction: TransactionEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity?,

    @Relation(
        parentColumn = "fromAssetId",
        entityColumn = "assetId"
    )
    val fromAsset: AssetEntity,

    @Relation(
        parentColumn = "toAssetId",
        entityColumn = "assetId"
    )
    val toAsset: AssetEntity?
)