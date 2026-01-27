package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import com.example.moneymanager.domain.usecase.asset.ReconcileAssetBalanceUseCase
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val reconcileAssetBalanceUseCase: ReconcileAssetBalanceUseCase
) {

    suspend operator fun invoke(transaction: Transaction): Resource<Unit> {
        try {
            if (transaction.amount <= 0) {
                return Resource.Error("Jumlah transaksi harus lebih dari 0")
            }

            val asset = assetRepository.getAssetById(transaction.fromAssetId)
                ?: return Resource.Error("Aset tidak ditemukan (ID: ${transaction.fromAssetId})")

            if (transaction.type == TransactionType.EXPENSE || transaction.type == TransactionType.TRANSFER_OUT) {
                if (asset.balance < transaction.amount) {
                    return Resource.Error("Saldo `${asset.name}` tidak mencukupi!")
                }
            }

            transactionRepository.insertTransaction(transaction)
            reconcileAssetBalanceUseCase(transaction.fromAssetId)

            return Resource.Success(Unit)

        } catch (e: Exception) {
            return Resource.Error("Gagal menyimpan transaksi: ${e.message}")
        }
    }
}