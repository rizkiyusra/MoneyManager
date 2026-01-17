package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.category.CategoryDao
import com.example.moneymanager.data.local.category.CategoryEntity
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Int): Category? =
        dao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        dao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        dao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        dao.deleteCategory(category.toEntity())

    private fun CategoryEntity.toDomain() = Category(
        id = categoryId,
        name = categoryName,
        description = categoryDescription,
        isIncomeCategory = isIncomeCategory,
        color = categoryColor,
        icon = categoryIcon,
        isSystemCategory = isSystemCategory,
        isActive = isActive,
        usageCount = usageCount,
        createdDate = createdDate
    )

    private fun Category.toEntity() = CategoryEntity(
        categoryId = id,
        categoryName = name,
        categoryDescription = description,
        isIncomeCategory = isIncomeCategory,
        categoryColor = color,
        categoryIcon = icon,
        isSystemCategory = isSystemCategory,
        isActive = isActive,
        usageCount = usageCount,
        createdDate = createdDate
    )
}
