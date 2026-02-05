package com.example.moneymanager.domain.usecase.report

import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryExpensesUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<CategoryExpense>> {
        return repository.getCategoryExpenses(startDate, endDate)
    }
}