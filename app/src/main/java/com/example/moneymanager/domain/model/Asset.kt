package com.example.moneymanager.domain.model

data class Asset(
    val id: Int = 0,
    val name: String,
    val type: String,
    val balance: Double,
    val unit: String,
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
