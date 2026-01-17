package com.example.moneymanager.domain.model

data class Transaction(
    val id: Int,
    val fromAssetId: Int,
    val toAssetId: Int? = null,
    val categoryId: Int? = null,
    val type: String,
    val category: String,
    val amount: Double,
    val currency: String,
    val convertedAmountIDR: Double,
    val exchangeRate: Double = 1.0,
    val title: String,
    val note: String? = null,
    val location: String? = null,
    val receiptImagePath: String? = null,
    val date: Long,
    val createdDate: Long
)
