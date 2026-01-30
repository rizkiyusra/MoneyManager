package com.example.moneymanager.domain.usecase.budget

import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.repository.BudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository,
    private val transactionDao: TransactionDao
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(month: Int, year: Int): Flow<List<Budget>> {
        val (startDate, endDate) = getMonthDateRange(month, year)

        return repository.getBudgets(month, year).flatMapLatest { budgets ->

            if (budgets.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = budgets.map { budget ->
                    transactionDao.getExpenseByCategoryAndDate(budget.categoryId, startDate, endDate)
                        .map { spentAmount ->
                            budget.copy(currentSpent = spentAmount)
                        }
                }
                combine(flows) { it.toList() }
            }
        }
    }

    private fun getMonthDateRange(month: Int, year: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val start = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }
}