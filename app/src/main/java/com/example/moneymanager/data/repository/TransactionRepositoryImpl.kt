package com.example.moneymanager.data.repository

import android.graphics.Color
import androidx.room.withTransaction
import com.example.moneymanager.data.local.MoneyManagerDatabase
import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.data.local.relation.TransactionWithDetails
import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.data.local.transfer.TransferLinkEntity
import com.example.moneymanager.data.local.transfer.TransferPairDao
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val db: MoneyManagerDatabase,
    private val transactionDao: TransactionDao,
    private val assetDao: AssetDao,
    private val transferPairDao: TransferPairDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> =
        transactionDao.getTransactionsWithDetails().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getTransactionById(id: Int): Transaction? =
        transactionDao.getTransactionById(id)?.toDomainSimple()

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction.toEntity())

    override suspend fun updateTransactionWithLogic(
        oldTransaction: Transaction,
        newTransaction: Transaction
    ) {
        db.withTransaction {
            val rollbackAmount = when (oldTransaction.type) {
                TransactionType.INCOME -> -oldTransaction.amount
                TransactionType.EXPENSE -> oldTransaction.amount
                TransactionType.TRANSFER_IN -> -oldTransaction.amount
                TransactionType.TRANSFER_OUT -> oldTransaction.amount
            }

            assetDao.updateAssetBalance(oldTransaction.fromAssetId, rollbackAmount)

            val newImpactAmount = when (newTransaction.type) {
                TransactionType.INCOME -> newTransaction.amount
                TransactionType.EXPENSE -> -newTransaction.amount
                TransactionType.TRANSFER_IN -> newTransaction.amount
                TransactionType.TRANSFER_OUT -> -newTransaction.amount
            }

            assetDao.updateAssetBalance(newTransaction.fromAssetId, newImpactAmount)

            transactionDao.updateTransaction(newTransaction.toEntity())
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransactionsByAssetId(assetId: Int) {
         transactionDao.deleteTransactionsByAssetId(assetId)
    }

    override suspend fun insertTransfer(
        sourceTransaction: Transaction,
        destinationTransaction: Transaction,
        sourceAsset: Asset,
        destinationAsset: Asset
    ) {
        db.withTransaction {
            val outId = transactionDao.insertTransaction(sourceTransaction.toEntity()).toInt()
            val inId = transactionDao.insertTransaction(destinationTransaction.toEntity()).toInt()

            assetDao.updateAsset(sourceAsset.toEntity())
            assetDao.updateAsset(destinationAsset.toEntity())

            val link = TransferLinkEntity(transferOutId = outId, transferInId = inId)
            transferPairDao.insertLink(link)
        }
    }

    private fun TransactionWithDetails.toDomain(): Transaction {
        return Transaction(
            id = transaction.transactionId,
            fromAssetId = transaction.fromAssetId,
            toAssetId = transaction.toAssetId,
            categoryId = transaction.categoryId,
            type = try { TransactionType.valueOf(transaction.transactionType) } catch (e: Exception) { TransactionType.EXPENSE },
            categoryName = category?.categoryName ?: "Tanpa Kategori",
            categoryIcon = category?.categoryIcon ?: "help",
            categoryColor = category?.categoryColor ?: Color.LTGRAY,
            fromAssetName = fromAsset.assetName,
            amount = transaction.transactionAmount,
            currency = transaction.transactionCurrency,
            convertedAmountIDR = transaction.convertedAmountIDR,
            exchangeRate = transaction.exchangeRate,
            title = transaction.transactionTitle,
            note = transaction.transactionNote,
            location = transaction.transactionLocation,
            receiptImagePath = transaction.receiptImagePath,
            date = transaction.transactionDate,
            createdDate = transaction.createdDate
        )
    }

    private fun TransactionEntity.toDomainSimple() = Transaction(
        id = transactionId,
        fromAssetId = fromAssetId,
        toAssetId = toAssetId,
        categoryId = categoryId,
        type = try { TransactionType.valueOf(transactionType) } catch (e: Exception) { TransactionType.EXPENSE },
        categoryName = "Loading...",
        categoryIcon = "help",
        categoryColor = Color.GRAY,
        fromAssetName = "Asset",
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
        transactionType = type.name,
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

    private fun Asset.toEntity() = AssetEntity(
        assetId = id,
        assetName = name,
        assetType = type,
        currentBalance = balance,
        balanceUnit = "IDR",
        currencySymbol = "Rp",
        accountNumber = null,
        bankName = null,
        lastPriceUpdate = null,
        priceSource = null,
        isActive = true,
        sortOrder = 0,
        createdDate = System.currentTimeMillis(),
        lastModified = System.currentTimeMillis()
    )
}