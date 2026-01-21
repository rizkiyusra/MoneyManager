package com.example.moneymanager.domain.repository

import com.example.moneymanager.domain.model.Asset
import kotlinx.coroutines.flow.Flow

interface AssetRepository {
    fun getAssets(): Flow<List<Asset>>
    suspend fun observeAssetById(id: Int): Flow<Asset?>
    suspend fun getAssetById(id: Int): Asset?
    suspend fun checkAssetNameExists(name: String, excludeId: Int): Int
    suspend fun reconcileAssetBalance(id: Int)
    suspend fun insertAsset(asset: Asset): Long
    suspend fun updateAsset(asset: Asset)
    suspend fun deleteAsset(asset: Asset)
}
