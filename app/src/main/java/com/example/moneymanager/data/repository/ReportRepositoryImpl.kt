package com.example.moneymanager.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.moneymanager.data.local.report.ReportDao
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao
) : ReportRepository {

    override fun getCategoryExpenses(startDate: Long, endDate: Long): Flow<List<CategoryExpense>> {
        return reportDao.getExpenseByCategory(startDate, endDate)
    }

    override fun getDailyTrend(startDate: Long, endDate: Long): Flow<List<DailySummary>> {
        return reportDao.getDailySummary(startDate, endDate)
    }

    override fun getCategoryMonthlyTrend(categoryId: Int): Flow<List<MonthlyCategoryTrend>> {
        return reportDao.getCategoryMonthlyTrend(categoryId)
    }

    override fun getTransactionsByCategory(categoryId: Int): Flow<List<Transaction>> {
        return reportDao.getTransactionsByCategory(
            categoryId = categoryId,
            startDate = 0L,
            endDate = Long.MAX_VALUE
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = transactionId,
            fromAssetId = fromAssetId,
            toAssetId = toAssetId,
            categoryId = categoryId,
            type = try { TransactionType.valueOf(transactionType) } catch (_: Exception) { TransactionType.EXPENSE },
            categoryName = "",
            categoryIcon = "history",
            categoryColor = Color.Gray.toArgb(),
            fromAssetName = "",
            amount = transactionAmount,
            currency = transactionCurrency,
            convertedAmountIDR = convertedAmountIDR,
            exchangeRate = exchangeRate,
            title = transactionTitle,
            note = transactionNote,
            location = transactionLocation,
            receiptImagePath = receiptImagePath,
            date = transactionDate,
            createdDate = createdDate
        )
    }
}