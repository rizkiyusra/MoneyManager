package com.example.moneymanager.domain.model

data class TransferPair(
    val id: Int = 0,
    val fromAssetId: Int,
    val toAssetId: Int,
    val name: String,
    val lastUsed: Long = System.currentTimeMillis(),
    val usageCount: Int = 0,
    val isQuickTransfer: Boolean = false,
    val createdDate: Long = System.currentTimeMillis()
)
