package com.example.moneymanager.domain.usecase.dashboard

import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

data class DashboardData(
    val totalBalance: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val recentTransactions: List<Transaction>
)

class GetDashboardDataUseCase @Inject constructor(
    private val assetRepository: AssetRepository,
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(): Flow<DashboardData> {
        return combine(
            assetRepository.getAssets(),
            transactionRepository.getTransactions()
        ) { assets, transactions ->

            val totalBalance = assets.sumOf { it.balance }
            val currentMonthTransactions = transactions.filter { isTransactionInCurrentMonth(it.date) }

            val monthlyIncome = currentMonthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val monthlyExpense = currentMonthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val recentTransactions = transactions

            DashboardData(
                totalBalance = totalBalance,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                recentTransactions = recentTransactions
            )
        }
    }

    private fun isTransactionInCurrentMonth(dateMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = dateMillis
        val transactionMonth = calendar.get(Calendar.MONTH)
        val transactionYear = calendar.get(Calendar.YEAR)

        return currentMonth == transactionMonth && currentYear == transactionYear
    }
}