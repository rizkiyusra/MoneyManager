package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import com.example.moneymanager.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getCategoryExpenses(startDate: Long, endDate: Long): Flow<List<CategoryExpense>>
    fun getDailyTrend(startDate: Long, endDate: Long): Flow<List<DailySummary>>
    fun getCategoryMonthlyTrend(categoryId: Int): Flow<List<MonthlyCategoryTrend>>
    fun getTransactionsByCategory(categoryId: Int): Flow<List<Transaction>>

}