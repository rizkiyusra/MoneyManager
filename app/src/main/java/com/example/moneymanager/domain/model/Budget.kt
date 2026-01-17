package com.example.moneymanager.domain.model

data class Budget(
    val id: Int = 0,
    val categoryId: Int,
    val name: String,
    val limit: Double,
    val period: String,
    val month: Int,
    val year: Int,
    val currentSpent: Double = 0.0,
    val isActive: Boolean = true,
    val alertThreshold: Double = 0.8,
    val createdDate: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
