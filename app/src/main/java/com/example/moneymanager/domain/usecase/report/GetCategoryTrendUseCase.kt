package com.example.moneymanager.domain.usecase.report

import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import com.example.moneymanager.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryTrendUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    operator fun invoke(categoryId: Int): Flow<List<MonthlyCategoryTrend>> {
        return repository.getCategoryMonthlyTrend(categoryId)
    }
}