package com.example.moneymanager.di

import com.example.moneymanager.data.repository.*
import com.example.moneymanager.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAssetRepository(
        assetRepositoryImpl: AssetRepositoryImpl
    ): AssetRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindPriceHistoryRepository(
        priceHistoryRepositoryImpl: PriceHistoryRepositoryImpl
    ): PriceHistoryRepository

    @Binds
    @Singleton
    abstract fun bindTransferPairRepository(
        transferPairRepositoryImpl: TransferPairRepositoryImpl
    ): TransferPairRepository

    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        backupMetadataRepositoryImpl: BackupMetadataRepositoryImpl
    ): BackupMetadataRepository

    @Binds
    @Singleton
    abstract fun bindRecurringRepository(
        recurringRepositoryImpl: RecurringRepositoryImpl
    ): RecurringRepository
}