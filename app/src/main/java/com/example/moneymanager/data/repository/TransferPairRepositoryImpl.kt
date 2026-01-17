package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.transfer.TransferPairDao
import com.example.moneymanager.data.local.transfer.TransferPairEntity
import com.example.moneymanager.domain.model.TransferPair
import com.example.moneymanager.domain.repository.TransferPairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransferPairRepositoryImpl @Inject constructor(
    private val dao: TransferPairDao
) : TransferPairRepository {

    override fun getPairs(): Flow<List<TransferPair>> =
        dao.getAllPairs().map { list -> list.map { it.toDomain() } }

    override suspend fun getPairById(id: Int): TransferPair? =
        dao.getPairById(id)?.toDomain()

    override suspend fun insertPair(pair: TransferPair): Long =
        dao.insertPair(pair.toEntity())

    override suspend fun updatePair(pair: TransferPair) =
        dao.updatePair(pair.toEntity())

    override suspend fun deletePair(pair: TransferPair) =
        dao.deletePair(pair.toEntity())

    private fun TransferPairEntity.toDomain() = TransferPair(
        id = pairId,
        fromAssetId = fromAssetId,
        toAssetId = toAssetId,
        name = pairName,
        lastUsed = lastUsed,
        usageCount = usageCount,
        isQuickTransfer = isQuickTransfer,
        createdDate = createdDate
    )

    private fun TransferPair.toEntity() = TransferPairEntity(
        pairId = id,
        fromAssetId = fromAssetId,
        toAssetId = toAssetId,
        pairName = name,
        lastUsed = lastUsed,
        usageCount = usageCount,
        isQuickTransfer = isQuickTransfer,
        createdDate = createdDate
    )
}
