package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(query: String): Flow<List<Transaction>> {
        return repository.searchTransactions(query)
    }
}