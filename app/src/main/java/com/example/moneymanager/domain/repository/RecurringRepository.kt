package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

interface RecurringRepository {
    fun getAllRecurringTransactions(): Flow<List<RecurringTransaction>>
    suspend fun insertRecurring(recurring: RecurringTransaction)
    suspend fun deleteRecurring(recurring: RecurringTransaction)
}