package com.example.moneymanager.data.local.asset

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY sortOrder ASC, assetId DESC")
    fun getAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE assetId = :id")
    fun observeAssetById(id: Int): Flow<AssetEntity?>

    @Query("SELECT * FROM assets WHERE assetId = :id")
    suspend fun getAssetById(id: Int): AssetEntity?

    @Query("SELECT COUNT(*) FROM assets WHERE assetName = :name AND assetId != :excludeId")
    suspend fun checkAssetNameExists(name: String, excludeId: Int = -1): Int

    @Query("UPDATE assets SET currentBalance = currentBalance + :amount WHERE assetId = :id")
    suspend fun updateAssetBalance(id: Int, amount: Double)

    @Query("UPDATE assets SET currentBalance = :newBalance WHERE assetId = :id")
    suspend fun setAssetBalance(id: Int, newBalance: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)
}
