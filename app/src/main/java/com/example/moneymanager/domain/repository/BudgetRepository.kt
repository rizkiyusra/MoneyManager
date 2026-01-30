package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgets(month: Int, year: Int): Flow<List<Budget>>
    suspend fun getBudgetById(id: Int): Budget?
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}
