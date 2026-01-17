package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.TransferPair
import kotlinx.coroutines.flow.Flow

interface TransferPairRepository {
    fun getPairs(): Flow<List<TransferPair>>
    suspend fun getPairById(id: Int): TransferPair?
    suspend fun insertPair(pair: TransferPair): Long
    suspend fun updatePair(pair: TransferPair)
    suspend fun deletePair(pair: TransferPair)
}
