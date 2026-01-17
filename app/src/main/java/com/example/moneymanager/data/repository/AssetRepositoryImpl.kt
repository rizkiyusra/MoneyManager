package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.asset.AssetEntity
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val dao: AssetDao
) : AssetRepository {

    override fun getAssets(): Flow<List<Asset>> =
        dao.getAssets().map { list -> list.map { it.toDomain() } }

    override suspend fun observeAssetById(id: Int): Flow<Asset?> =
        dao.observeAssetById(id).map { it?.toDomain() }

    override suspend fun getAssetById(id: Int): Asset? =
        dao.getAssetById(id)?.toDomain()

    override suspend fun checkAssetNameExists(name: String, excludeId: Int): Int =
        dao.checkAssetNameExists(name, excludeId)

    override suspend fun insertAsset(asset: Asset): Long =
        dao.insertAsset(asset.toEntity())

    override suspend fun updateAsset(asset: Asset) =
        dao.updateAsset(asset.toEntity())

    override suspend fun deleteAsset(asset: Asset) =
        dao.deleteAsset(asset.toEntity())

    private fun AssetEntity.toDomain() = Asset(
        id = assetId,
        name = assetName,
        type = assetType,
        balance = currentBalance,
        unit = balanceUnit,
        currencySymbol = currencySymbol,
        accountNumber = accountNumber,
        bankName = bankName,
        lastPriceUpdate = lastPriceUpdate,
        priceSource = priceSource,
        isActive = isActive,
        sortOrder = sortOrder,
        createdDate = createdDate,
        lastModified = lastModified
    )

    private fun Asset.toEntity() = AssetEntity(
        assetId = id,
        assetName = name,
        assetType = type,
        currentBalance = balance,
        balanceUnit = unit,
        currencySymbol = currencySymbol,
        accountNumber = accountNumber,
        bankName = bankName,
        lastPriceUpdate = lastPriceUpdate,
        priceSource = priceSource,
        isActive = isActive,
        sortOrder = sortOrder,
        createdDate = createdDate,
        lastModified = lastModified
    )
}
