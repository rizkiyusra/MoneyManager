package com.example.moneymanager.data.local.recurring

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {
    @Query("SELECT * FROM recurring_transactions WHERE nextRunDate <= :currentTime")
    suspend fun getPendingRecurringTransactions(currentTime: Long): List<RecurringTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurring(recurring: RecurringTransactionEntity)

    @Update
    suspend fun updateRecurring(recurring: RecurringTransactionEntity)

    @Query("SELECT * FROM recurring_transactions")
    fun getAllRecurringTransactions(): Flow<List<RecurringTransactionEntity>>

    @androidx.room.Delete
    suspend fun deleteRecurring(recurring: RecurringTransactionEntity)
}