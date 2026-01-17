package com.example.moneymanager.data.local.budget

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moneymanager.data.local.category.CategoryEntity

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["budgetCategoryId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("budgetCategoryId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val budgetId: Int = 0,
    val budgetCategoryId: Int,
    val budgetName: String,
    val budgetLimit: Double,
    val budgetPeriod: String,
    val budgetMonth: Int,
    val budgetYear: Int,
    val currentSpent: Double = 0.0,
    val isActive: Boolean = true,
    val alertThreshold: Double = 0.8,
    val createdDate: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
