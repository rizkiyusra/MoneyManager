package com.example.moneymanager.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER_IN,
    TRANSFER_OUT,
}

data class Transaction(
    val id: Int = 0,
    val fromAssetId: Int,
    val toAssetId: Int? = null,
    val categoryId: Int? = null,
    val type: TransactionType,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Int,
    val fromAssetName: String,
    val amount: Double,
    val currency: String,
    val convertedAmountIDR: Double,
    val exchangeRate: Double = 1.0,
    val title: String,
    val note: String? = null,
    val location: String? = null,
    val receiptImagePath: String? = null,
    val date: Long,
    val createdDate: Long = System.currentTimeMillis()
)
