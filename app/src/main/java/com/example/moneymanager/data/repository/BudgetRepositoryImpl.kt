package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.budget.BudgetDao
import com.example.moneymanager.data.local.budget.BudgetEntity
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao
) : BudgetRepository {

    override fun getBudgets(): Flow<List<Budget>> =
        dao.getAllBudgets().map { list -> list.map { it.toDomain() } }

    override suspend fun getBudgetById(id: Int): Budget? =
        dao.getBudgetById(id)?.toDomain()

    override suspend fun insertBudget(budget: Budget): Long =
        dao.insertBudget(budget.toEntity())

    override suspend fun updateBudget(budget: Budget) =
        dao.updateBudget(budget.toEntity())

    override suspend fun deleteBudget(budget: Budget) =
        dao.deleteBudget(budget.toEntity())

    private fun BudgetEntity.toDomain() = Budget(
        id = budgetId,
        categoryId = budgetCategoryId,
        name = budgetName,
        limit = budgetLimit,
        period = budgetPeriod,
        month = budgetMonth,
        year = budgetYear,
        currentSpent = 0.0,
        isActive = isActive,
        alertThreshold = alertThreshold,
        createdDate = createdDate,
        lastUpdated = System.currentTimeMillis()
    )

    // Mapping Domain -> Entity
    private fun Budget.toEntity() = BudgetEntity(
        budgetId = id,
        budgetCategoryId = categoryId,
        budgetName = name,
        budgetLimit = limit,
        budgetPeriod = period,
        budgetMonth = month,
        budgetYear = year,
        isActive = isActive,
        alertThreshold = alertThreshold,
        createdDate = createdDate,
    )
}
