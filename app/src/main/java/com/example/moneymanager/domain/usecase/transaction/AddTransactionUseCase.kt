package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository
) {

    suspend operator fun invoke(transaction: Transaction): Resource<Unit> {
        try {
            if (transaction.amount <= 0) {
                return Resource.Error("Jumlah transaksi harus lebih dari 0")
            }

            val asset = assetRepository.getAssetById(transaction.fromAssetId)
                ?: return Resource.Error("Aset tidak ditemukan (ID: ${transaction.fromAssetId})")

            when (transaction.type) {
                TransactionType.EXPENSE, TransactionType.TRANSFER_OUT -> {
                    if (asset.balance < transaction.amount) {
                        return Resource.Error("Saldo `${asset.name}` tidak mencukupi!")
                    }
                }
                else -> {}
            }

            val newBalance = when (transaction.type) {
                TransactionType.INCOME -> asset.balance + transaction.amount
                TransactionType.EXPENSE -> asset.balance - transaction.amount
                TransactionType.TRANSFER_OUT -> asset.balance - transaction.amount
                TransactionType.TRANSFER_IN -> asset.balance + transaction.amount
            }

            assetRepository.updateAsset(asset.copy(balance = newBalance))
            transactionRepository.insertTransaction(transaction)

            return Resource.Success(Unit)

        } catch (e: Exception) {
            return Resource.Error("Gagal menyimpan transaksi: ${e.message}")
        }
    }
}