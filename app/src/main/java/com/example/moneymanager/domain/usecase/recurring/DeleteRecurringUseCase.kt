package com.example.moneymanager.domain.usecase.recurring

import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.domain.repository.RecurringRepository
import javax.inject.Inject

class DeleteRecurringUseCase @Inject constructor(
    private val repository: RecurringRepository
) {
    suspend operator fun invoke(recurring: RecurringTransaction) {
        repository.deleteRecurring(recurring)
    }
}