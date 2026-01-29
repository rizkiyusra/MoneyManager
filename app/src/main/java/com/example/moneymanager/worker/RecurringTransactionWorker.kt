package com.example.moneymanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.data.local.recurring.RecurringTransactionDao
import com.example.moneymanager.domain.repository.CategoryRepository
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val recurringDao: RecurringTransactionDao,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()
            val pendingList = recurringDao.getPendingRecurringTransactions(currentTime)

            if (pendingList.isEmpty()) {
                return Result.success()
            }

            pendingList.forEach { recurring ->
                val category = categoryRepository.getCategoryById(recurring.categoryId)

                val newTransaction = Transaction(
                    id = 0,
                    amount = recurring.amount,
                    note = recurring.note,
                    type = if (recurring.isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                    date = currentTime,
                    categoryId = recurring.categoryId,
                    categoryName = category?.name ?: "Auto",
                    categoryIcon = category?.icon ?: "history",
                    categoryColor = category?.color ?: -7829368,
                    fromAssetId = recurring.assetId,
                    fromAssetName = "",
                    title = recurring.note.ifBlank { "Transaksi Otomatis" },
                    currency = "IDR",
                    convertedAmountIDR = recurring.amount,
                    exchangeRate = 1.0,
                    location = null,
                    receiptImagePath = null,
                    createdDate = currentTime
                )

                addTransactionUseCase(newTransaction)

                val nextDate = calculateNextRunDate(recurring.nextRunDate, recurring.frequency)

                val updatedRecurring = recurring.copy(
                    nextRunDate = nextDate
                )
                recurringDao.updateRecurring(updatedRecurring)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun calculateNextRunDate(currentDate: Long, frequency: String): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDate

        when (frequency) {
            "DAILY" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
            "YEARLY" -> calendar.add(Calendar.YEAR, 1)
            else -> calendar.add(Calendar.MONTH, 1)
        }
        return calendar.timeInMillis
    }
}