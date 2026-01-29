package com.example.moneymanager.domain.usecase.recurring

import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.domain.repository.RecurringRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetRecurringTransactionsUseCase @Inject constructor(
    private val repository: RecurringRepository
) {
    operator fun invoke(): Flow<List<RecurringTransaction>> {
        return repository.getAllRecurringTransactions()
    }
}