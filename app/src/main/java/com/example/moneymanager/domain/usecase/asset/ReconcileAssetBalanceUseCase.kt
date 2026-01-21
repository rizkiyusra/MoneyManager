package com.example.moneymanager.domain.usecase.asset

import com.example.moneymanager.domain.repository.AssetRepository
import javax.inject.Inject

class ReconcileAssetBalanceUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) {
    suspend operator fun invoke(assetId: Int) {
        assetRepository.reconcileAssetBalance(assetId)
    }
}