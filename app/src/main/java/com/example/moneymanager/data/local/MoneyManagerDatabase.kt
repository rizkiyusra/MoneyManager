package com.example.moneymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.data.local.backup.BackupMetadataDao
import com.example.moneymanager.data.local.backup.BackupMetadataEntity
import com.example.moneymanager.data.local.budget.BudgetDao
import com.example.moneymanager.data.local.budget.BudgetEntity
import com.example.moneymanager.data.local.category.CategoryDao
import com.example.moneymanager.data.local.category.CategoryEntity
import com.example.moneymanager.data.local.price.PriceHistoryDao
import com.example.moneymanager.data.local.price.PriceHistoryEntity
import com.example.moneymanager.data.local.recurring.RecurringTransactionDao
import com.example.moneymanager.data.local.recurring.RecurringTransactionEntity
import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.data.local.transfer.TransferLinkEntity
import com.example.moneymanager.data.local.transfer.TransferPairDao
import com.example.moneymanager.data.local.transfer.TransferPairEntity


@Database(
    entities = [
        AssetEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        PriceHistoryEntity::class,
        TransferPairEntity::class,
        TransferLinkEntity::class,
        BackupMetadataEntity::class,
        RecurringTransactionEntity::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class MoneyManagerDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun transferPairDao(): TransferPairDao
    abstract fun backupMetadataDao(): BackupMetadataDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
}
