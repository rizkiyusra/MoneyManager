package com.example.moneymanager.domain.usecase.report

import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryTransactionsUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    operator fun invoke(categoryId: Int): Flow<List<Transaction>> {
        return repository.getTransactionsByCategory(categoryId)
    }
}