package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.recurring.RecurringTransactionDao
import com.example.moneymanager.data.local.recurring.RecurringTransactionEntity
import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.domain.repository.RecurringRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecurringRepositoryImpl @Inject constructor(
    private val dao: RecurringTransactionDao
) : RecurringRepository {

    override fun getAllRecurringTransactions(): Flow<List<RecurringTransaction>> {
        return dao.getAllRecurringTransactions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertRecurring(recurring: RecurringTransaction) {
        dao.insertRecurring(recurring.toEntity())
    }

    override suspend fun deleteRecurring(recurring: RecurringTransaction) {
        dao.deleteRecurring(recurring.toEntity())
    }

    private fun RecurringTransactionEntity.toDomain() = RecurringTransaction(
        id = id,
        amount = amount,
        note = note,
        isIncome = isIncome,
        categoryId = categoryId,
        assetId = assetId,
        frequency = frequency,
        nextRunDate = nextRunDate,
        createdDate = createdDate
    )

    private fun RecurringTransaction.toEntity() = RecurringTransactionEntity(
        id = id,
        amount = amount,
        note = note,
        isIncome = isIncome,
        categoryId = categoryId,
        assetId = assetId,
        frequency = frequency,
        nextRunDate = nextRunDate,
        createdDate = createdDate
    )
}