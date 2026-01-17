package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.PriceHistory
import kotlinx.coroutines.flow.Flow

interface PriceHistoryRepository {
    fun getPriceHistory(currencyCode: String): Flow<List<PriceHistory>>
    suspend fun getLatestPrice(currencyCode: String): PriceHistory?
    suspend fun insertPrice(price: PriceHistory): Long
}
