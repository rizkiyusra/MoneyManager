package com.example.moneymanager.domain.usecase.asset

import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.repository.AssetRepository
import javax.inject.Inject

class GetAssetByIdUseCase @Inject constructor(
    private val repository: AssetRepository
) {
    suspend operator fun invoke(id: Int): Asset? {
        return repository.getAssetById(id)
    }
}