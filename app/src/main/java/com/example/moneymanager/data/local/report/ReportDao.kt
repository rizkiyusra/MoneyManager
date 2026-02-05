package com.example.moneymanager.data.local.report

import androidx.room.Dao
import androidx.room.Query
import com.example.moneymanager.data.local.transaction.TransactionEntity
import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("""
        SELECT 
            c.categoryId,
            c.categoryName,
            SUM(t.transactionAmount) as totalAmount,
            c.categoryColor as color,
            c.categoryIcon as icon
        FROM transactions t
        JOIN categories c ON t.categoryId = c.categoryId
        WHERE t.transactionType = 'EXPENSE'
        AND t.transactionDate BETWEEN :startDate AND :endDate
        GROUP BY t.categoryId
        ORDER BY totalAmount DESC
    """)
    fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<CategoryExpense>>

    @Query("""
        SELECT 
            t.transactionDate as date,
            SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.transactionAmount ELSE 0.0 END) as income,
            SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.transactionAmount ELSE 0.0 END) as expense
        FROM transactions t
        WHERE t.transactionDate BETWEEN :startDate AND :endDate
        GROUP BY strftime('%Y-%m-%d', t.transactionDate / 1000, 'unixepoch', 'localtime')
        ORDER BY t.transactionDate ASC
    """)
    fun getDailySummary(startDate: Long, endDate: Long): Flow<List<DailySummary>>

    @Query("""
        SELECT 
            strftime('%Y-%m', transactionDate / 1000, 'unixepoch', 'localtime') as monthYear,
            SUM(transactionAmount) as total
        FROM transactions 
        WHERE categoryId = :categoryId 
        AND transactionType = 'EXPENSE'
        GROUP BY monthYear
        ORDER BY monthYear ASC
        LIMIT 12
    """)
    fun getCategoryMonthlyTrend(categoryId: Int): Flow<List<MonthlyCategoryTrend>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    fun getTransactionsByCategory(categoryId: Int, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>
}