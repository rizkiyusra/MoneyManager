package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> =
        dao.getTransactions().map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Int): Transaction? =
        dao.getTransactionById(id)?.toDomain()

    override suspend fun insertTransaction(transaction: Transaction): Long =
        dao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        dao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.toEntity())

    private fun TransactionEntity.toDomain() = Transaction(
        id = transactionId,
        fromAssetId = fromAssetId,
        toAssetId = toAssetId,
        categoryId = categoryId,
        type = transactionType,
        category = transactionCategory,
        amount = transactionAmount,
        currency = transactionCurrency,
        convertedAmountIDR = convertedAmountIDR,
        exchangeRate = exchangeRate,
        title = transactionTitle,
        note = transactionNote,
        location = transactionLocation,
        receiptImagePath = receiptImagePath,
        date = transactionDate,
        createdDate = createdDate
    )

    private fun Transaction.toEntity() = TransactionEntity(
        transactionId = id,
        fromAssetId = fromAssetId,
        toAssetId = toAssetId,
        categoryId = categoryId,
        transactionType = type,
        transactionCategory = category,
        transactionAmount = amount,
        transactionCurrency = currency,
        convertedAmountIDR = convertedAmountIDR,
        exchangeRate = exchangeRate,
        transactionTitle = title,
        transactionNote = note,
        transactionLocation = location,
        receiptImagePath = receiptImagePath,
        transactionDate = date,
        createdDate = createdDate
    )
}
