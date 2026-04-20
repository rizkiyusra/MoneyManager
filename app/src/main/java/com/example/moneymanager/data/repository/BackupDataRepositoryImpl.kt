package com.example.moneymanager.data.repository

import androidx.room.withTransaction
import com.example.moneymanager.data.local.MoneyManagerDatabase
import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.data.local.category.CategoryDao
import com.example.moneymanager.data.local.category.CategoryEntity
import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.BackupData
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.BackupDataRepository
import javax.inject.Inject

class BackupDataRepositoryImpl @Inject constructor(
    private val db: MoneyManagerDatabase,
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) : BackupDataRepository {

    override suspend fun createBackupData(): BackupData {
        val assets = assetDao.getAllAssetsSync().map { it.toDomain() }
        val categories = categoryDao.getAllCategoriesSync().map { it.toDomain() }
        val transactions = transactionDao.getAllTransactionsSync().map { it.toDomain() }

        return BackupData(
            version = 1,
            exportDate = System.currentTimeMillis(),
            assets = assets,
            categories = categories,
            transactions = transactions
        )
    }

    override suspend fun restoreBackupData(backupData: BackupData) {
        db.withTransaction {
            transactionDao.deleteAllTransactions()
            categoryDao.deleteAllCategories()
            assetDao.deleteAllAssets()

            assetDao.insertAllAssets(backupData.assets.map { it.toEntity() })
            categoryDao.insertAllCategories(backupData.categories.map { it.toEntity() })
            transactionDao.insertAllTransactions(backupData.transactions.map { it.toEntity() })
        }
    }
    private fun AssetEntity.toDomain() = Asset(
        id = this.assetId,
        name = this.assetName,
        type = this.assetType,
        balance = this.currentBalance,
        unit = this.balanceUnit,
        currencySymbol = this.currencySymbol,
        accountNumber = this.accountNumber,
        bankName = this.bankName,
        lastPriceUpdate = this.lastPriceUpdate,
        priceSource = this.priceSource,
        isActive = this.isActive,
        sortOrder = this.sortOrder,
        createdDate = this.createdDate,
        lastModified = this.lastModified
    )

    private fun Asset.toEntity() = AssetEntity(
        assetId = this.id,
        assetName = this.name,
        assetType = this.type,
        currentBalance = this.balance,
        balanceUnit = this.unit,
        currencySymbol = this.currencySymbol,
        accountNumber = this.accountNumber,
        bankName = this.bankName,
        lastPriceUpdate = this.lastPriceUpdate,
        priceSource = this.priceSource,
        isActive = this.isActive,
        sortOrder = this.sortOrder,
        createdDate = this.createdDate,
        lastModified = this.lastModified
    )

    private fun CategoryEntity.toDomain() = Category(
        id = this.categoryId,
        name = this.categoryName,
        description = this.categoryDescription,
        color = this.categoryColor,
        icon = this.categoryIcon,
        isActive = this.isActive,
        usageCount = this.usageCount,
        createdDate = this.createdDate
    )

    private fun Category.toEntity() = CategoryEntity(
        categoryId = this.id,
        categoryName = this.name,
        categoryDescription = this.description,
        categoryColor = this.color,
        categoryIcon = this.icon,
        isActive = this.isActive,
        usageCount = this.usageCount,
        createdDate = this.createdDate
    )

    private fun TransactionEntity.toDomain() = Transaction(
        id = this.transactionId,
        fromAssetId = this.fromAssetId,
        toAssetId = this.toAssetId,
        categoryId = this.categoryId,
        type = TransactionType.valueOf(this.transactionType),
        amount = this.transactionAmount,
        currency = this.transactionCurrency,
        convertedAmountIDR = this.convertedAmountIDR,
        exchangeRate = this.exchangeRate,
        title = this.transactionTitle,
        note = this.transactionNote,
        location = this.transactionLocation,
        receiptImagePath = this.receiptImagePath,
        date = this.transactionDate,
        categoryColor = 0,
        categoryIcon = "",
        categoryName = "",
        fromAssetName = "",
        createdDate = this.createdDate
    )

    private fun Transaction.toEntity() = TransactionEntity(
        transactionId = this.id,
        fromAssetId = this.fromAssetId,
        toAssetId = this.toAssetId,
        categoryId = this.categoryId,
        transactionType = this.type.name,
        transactionAmount = this.amount,
        transactionCurrency = this.currency,
        convertedAmountIDR = this.convertedAmountIDR,
        exchangeRate = this.exchangeRate,
        transactionTitle = this.title,
        transactionNote = this.note,
        transactionLocation = this.location,
        receiptImagePath = this.receiptImagePath,
        transactionDate = this.date,
        createdDate = this.createdDate
    )
}