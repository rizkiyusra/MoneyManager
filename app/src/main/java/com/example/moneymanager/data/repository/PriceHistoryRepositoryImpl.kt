package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.price.PriceHistoryDao
import com.example.moneymanager.data.local.price.PriceHistoryEntity
import com.example.moneymanager.domain.model.PriceHistory
import com.example.moneymanager.domain.repository.PriceHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PriceHistoryRepositoryImpl @Inject constructor(
    private val dao: PriceHistoryDao
) : PriceHistoryRepository {

    override fun getPriceHistory(currencyCode: String): Flow<List<PriceHistory>> =
        dao.getPriceHistoryByCurrency(currencyCode).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getLatestPrice(currencyCode: String): PriceHistory? =
        dao.getLatestPrice(currencyCode)?.toDomain()

    override suspend fun insertPrice(price: PriceHistory): Long {
        dao.clearLatest(price.currencyCode)
        return dao.insertPrice(price.toEntity(isLatest = true))
    }

    private fun PriceHistoryEntity.toDomain() = PriceHistory(
        id = priceId,
        currencyCode = currencyCode,
        priceInIDR = priceInIDR,
        priceInUSD = priceInUSD,
        source = priceSource,
        date = priceDate,
        isLatest = isLatest
    )

    private fun PriceHistory.toEntity(isLatest: Boolean = false) = PriceHistoryEntity(
        priceId = id,
        currencyCode = currencyCode,
        priceInIDR = priceInIDR,
        priceInUSD = priceInUSD,
        priceSource = source,
        priceDate = date,
        isLatest = isLatest
    )
}
