package com.example.moneymanager.data.local.price

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Query("SELECT * FROM price_history WHERE currencyCode = :code ORDER BY priceDate DESC")
    fun getPriceHistoryByCurrency(code: String): Flow<List<PriceHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: PriceHistoryEntity): Long

    @Query("UPDATE price_history SET isLatest = 0 WHERE currencyCode = :code")
    suspend fun clearLatest(code: String)

    @Query("SELECT * FROM price_history WHERE currencyCode = :code AND isLatest = 1 LIMIT 1")
    suspend fun getLatestPrice(code: String): PriceHistoryEntity?
}
