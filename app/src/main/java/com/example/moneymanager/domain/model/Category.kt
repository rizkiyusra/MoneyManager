package com.example.moneymanager.domain.model

data class Category(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val isIncomeCategory: Boolean = false,
    val color: Int,
    val icon: String,
    val isSystemCategory: Boolean = false,
    val isActive: Boolean = true,
    val usageCount: Int = 0,
    val createdDate: Long = System.currentTimeMillis()
)
