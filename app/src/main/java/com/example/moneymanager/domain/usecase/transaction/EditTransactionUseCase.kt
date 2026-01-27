package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import com.example.moneymanager.domain.usecase.asset.ReconcileAssetBalanceUseCase
import javax.inject.Inject

class EditTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val reconcileAssetBalanceUseCase: ReconcileAssetBalanceUseCase
) {
    suspend operator fun invoke(newTransaction: Transaction): Resource<Unit> {
        return try {
            if (newTransaction.amount <= 0) {
                return Resource.Error("Jumlah transaksi harus lebih dari 0")
            }
            if (newTransaction.title.isBlank()) {
                return Resource.Error("Catatan/Judul tidak boleh kosong")
            }

            val oldTransaction = transactionRepository.getTransactionById(newTransaction.id)
                ?: return Resource.Error("Transaksi lama tidak ditemukan!")

            if (newTransaction.type == TransactionType.EXPENSE && oldTransaction.fromAssetId != newTransaction.fromAssetId) {
                val targetAsset = assetRepository.getAssetById(newTransaction.fromAssetId)

                if ((targetAsset?.balance ?: 0.0) < newTransaction.amount) {
                    return Resource.Error("Saldo '${targetAsset?.name}' tidak cukup untuk menampung transaksi ini!")
                }
            }

            if (newTransaction.type == TransactionType.EXPENSE && oldTransaction.fromAssetId == newTransaction.fromAssetId) {
                val currentAsset = assetRepository.getAssetById(newTransaction.fromAssetId)
                val realBalance = (currentAsset?.balance ?: 0.0) + oldTransaction.amount

                if (realBalance < newTransaction.amount) {
                    return Resource.Error("Saldo tidak cukup untuk nominal baru!")
                }
            }

            transactionRepository.updateTransaction(newTransaction)
            reconcileAssetBalanceUseCase(newTransaction.fromAssetId)

            if (oldTransaction.fromAssetId != newTransaction.fromAssetId) {
                reconcileAssetBalanceUseCase(oldTransaction.fromAssetId)
            }

            if (newTransaction.toAssetId != null) {
                reconcileAssetBalanceUseCase(newTransaction.toAssetId)

                if (oldTransaction.toAssetId != newTransaction.toAssetId && oldTransaction.toAssetId != null) {
                    reconcileAssetBalanceUseCase(oldTransaction.toAssetId)
                }
            }

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error("Gagal update transaksi: ${e.localizedMessage}")
        }
    }
}