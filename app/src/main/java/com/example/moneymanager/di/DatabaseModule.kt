package com.example.moneymanager.di

import android.content.Context
import androidx.room.Room
import com.example.moneymanager.data.local.MoneyManagerDatabase
import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.backup.BackupMetadataDao
import com.example.moneymanager.data.local.budget.BudgetDao
import com.example.moneymanager.data.local.callback.DatabaseSeeder
import com.example.moneymanager.data.local.category.CategoryDao
import com.example.moneymanager.data.local.price.PriceHistoryDao
import com.example.moneymanager.data.local.recurring.RecurringTransactionDao
import com.example.moneymanager.data.local.report.ReportDao
import com.example.moneymanager.data.local.transaction.TransactionDao
import com.example.moneymanager.data.local.transfer.TransferPairDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideMoneyManagerDatabase(
        @ApplicationContext context: Context,
        databaseSeeder: Provider<DatabaseSeeder>
    ): MoneyManagerDatabase {
        return Room.databaseBuilder(
            context,
            MoneyManagerDatabase::class.java,
            "money_manager_db"
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(databaseSeeder.get())
            .build()
    }

    @Provides
    fun provideAssetDao(db: MoneyManagerDatabase): AssetDao {
        return db.assetDao()
    }

    @Provides
    fun provideCategoryDao(db: MoneyManagerDatabase): CategoryDao {
        return db.categoryDao()
    }

    @Provides
    fun provideBudgetDao(db: MoneyManagerDatabase): BudgetDao {
        return db.budgetDao()
    }

    @Provides
    fun provideTransactionDao(db: MoneyManagerDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    fun provideTransferPairDao(db: MoneyManagerDatabase): TransferPairDao {
        return db.transferPairDao()
    }

    @Provides
    fun providePriceHistoryDao(db: MoneyManagerDatabase): PriceHistoryDao {
        return db.priceHistoryDao()
    }

    @Provides
    fun provideBackupMetadataDao(db: MoneyManagerDatabase): BackupMetadataDao {
        return db.backupMetadataDao()
    }

    @Provides
    fun provideRecurringTransactionDao(db: MoneyManagerDatabase): RecurringTransactionDao {
        return db.recurringTransactionDao()
    }

    @Provides
    @Singleton
    fun provideReportDao(db: MoneyManagerDatabase): ReportDao {
        return db.reportDao()
    }
}