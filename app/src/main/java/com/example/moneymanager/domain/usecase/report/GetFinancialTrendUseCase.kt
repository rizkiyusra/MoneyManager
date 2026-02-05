package com.example.moneymanager.domain.usecase.report

import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFinancialTrendUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<DailySummary>> {
        return repository.getDailyTrend(startDate, endDate)
    }
}