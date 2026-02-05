package com.example.moneymanager.domain.model

data class CategoryExpense(
    val categoryId: Int,
    val categoryName: String,
    val totalAmount: Double,
    val color: Int,
    val icon: String
)