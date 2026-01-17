package com.example.moneymanager.data.local.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val categoryName: String,
    val categoryDescription: String? = null,
    val isIncomeCategory: Boolean = false,
    val categoryColor: Int,
    val categoryIcon: String,
    val isSystemCategory: Boolean = false,
    val isActive: Boolean = true,
    val usageCount: Int = 0,
    val createdDate: Long = System.currentTimeMillis()
)
