package com.example.moneymanager.domain.model

data class TransactionFilter(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val transactionType: TransactionType? = null,
    val categoryId: Int? = null,
    val assetId: Int? = null
)
