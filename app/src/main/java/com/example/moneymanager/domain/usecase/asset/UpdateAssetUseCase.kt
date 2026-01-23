package com.example.moneymanager.domain.usecase.asset

import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.repository.AssetRepository
import javax.inject.Inject

class UpdateAssetUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(asset: Asset) {
        repository.updateAsset(asset)
    }
}