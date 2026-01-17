package com.example.moneymanager.data.local.transfer

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferPairDao {
    @Query("SELECT * FROM transfer_pairs ORDER BY usageCount DESC")
    fun getAllPairs(): Flow<List<TransferPairEntity>>

    @Query("SELECT * FROM transfer_pairs WHERE pairId = :id")
    suspend fun getPairById(id: Int): TransferPairEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPair(pair: TransferPairEntity): Long

    @Update
    suspend fun updatePair(pair: TransferPairEntity)

    @Delete
    suspend fun deletePair(pair: TransferPairEntity)
}
