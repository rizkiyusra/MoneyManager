package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class EditTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository
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

            val asset = assetRepository.getAssetById(newTransaction.fromAssetId)
                ?: return Resource.Error("Aset tidak ditemukan!")

            if (newTransaction.type == TransactionType.EXPENSE) {
                val virtualBalance = asset.balance + oldTransaction.amount

                if (virtualBalance < newTransaction.amount) {
                    return Resource.Error("Saldo tidak mencukupi untuk perubahan ini!")
                }
            }

            transactionRepository.updateTransactionWithLogic(oldTransaction, newTransaction)

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error("Gagal update transaksi: ${e.localizedMessage}")
        }
    }
}