package com.example.moneymanager.data.local.transaction

import androidx.room.*
import com.example.moneymanager.data.local.relation.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getTransactions(): Flow<List<TransactionEntity>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM transactions WHERE transactionId = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}
