package com.example.moneymanager.data.local.category

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY isSystemCategory DESC, usageCount DESC, categoryName ASC")
    fun getAllActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    fun getAllCategoriesRaw(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("UPDATE categories SET isActive = 0 WHERE categoryId = :id")
    suspend fun softDeleteCategory(id: Int)

    @Query("UPDATE categories SET usageCount = usageCount + 1 WHERE categoryId = :id")
    suspend fun incrementUsageCount(id: Int)
}
