package com.example.moneymanager.domain.usecase.asset

import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class AddAssetUseCase @Inject constructor(
    private val assetRepository: AssetRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(asset: Asset, initialBalance: Double) {
        val assetWithBalance = asset.copy(balance = initialBalance)
        val newAssetId = assetRepository.insertAsset(assetWithBalance)

        if (initialBalance > 0) {
            val transaction = Transaction(
                id = 0,
                amount = initialBalance,
                note = "Saldo Awal",
                type = TransactionType.INCOME,
                date = System.currentTimeMillis(),
                categoryId = null,
                fromAssetId = newAssetId.toInt(),
                title = "Saldo Awal",
                categoryName = "",
                categoryIcon = "",
                categoryColor = 0,
                fromAssetName = "",
                currency = "IDR",
                convertedAmountIDR = initialBalance,
                exchangeRate = 1.0,
                location = null,
                receiptImagePath = null,
                createdDate = System.currentTimeMillis()
            )
            transactionRepository.insertTransaction(transaction)
        }
    }
}