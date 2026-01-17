package com.example.moneymanager.domain.model

data class PriceHistory(
    val id: Int = 0,
    val currencyCode: String,
    val priceInIDR: Double,
    val priceInUSD: Double,
    val source: String,
    val date: Long,
    val isLatest: Boolean = false
)
