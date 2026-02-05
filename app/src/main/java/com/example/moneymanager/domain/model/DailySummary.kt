package com.example.moneymanager.domain.model

data class DailySummary(
    val date: Long,
    val income: Double,
    val expense: Double
)