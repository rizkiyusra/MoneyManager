package com.example.moneymanager.domain.usecase.asset

import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteAssetUseCase @Inject constructor(
    private val repository: AssetRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(asset: Asset) {
        transactionRepository.deleteTransactionsByAssetId(asset.id)
        repository.deleteAsset(asset)
    }
}