package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    fun getAllCategoriesIncludeDeleted(): Flow<List<Category>>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(id: Int)
    suspend fun incrementUsage(id: Int)
}
