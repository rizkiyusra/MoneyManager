package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val assetRepository: AssetRepository
) {
    suspend operator fun invoke(transaction: Transaction): Resource<Unit> {
        return try {
            val asset = assetRepository.getAssetById(transaction.fromAssetId)
                ?: return Resource.Error("Aset tidak ditemukan, transaksi tetap dihapus.")

            val newBalance = when (transaction.type) {
                TransactionType.INCOME -> asset.balance - transaction.amount
                TransactionType.EXPENSE -> asset.balance + transaction.amount
                TransactionType.TRANSFER_OUT -> asset.balance + transaction.amount
                TransactionType.TRANSFER_IN -> asset.balance - transaction.amount
            }

            assetRepository.updateAsset(asset.copy(balance = newBalance))
            repository.deleteTransaction(transaction)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus: ${e.message}")
        }
    }
}