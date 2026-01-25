package com.example.moneymanager.domain.usecase.transaction

import android.graphics.Color
import com.example.moneymanager.common.extension.toRupiah
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransferUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository
) {

    suspend operator fun invoke(
        amount: Double,
        note: String,
        date: Long,
        fromAssetId: Int,
        toAssetId: Int
    ): Resource<Unit> {
        return try {
            if (amount <= 0) {
                return Resource.Error("Jumlah transfer harus lebih dari 0")
            }
            if (fromAssetId == toAssetId) {
                return Resource.Error("Aset asal dan tujuan tidak boleh sama")
            }

            val sourceAsset = assetRepository.getAssetById(fromAssetId)
                ?: return Resource.Error("Aset asal tidak ditemukan")

            val destAsset = assetRepository.getAssetById(toAssetId)
                ?: return Resource.Error("Aset tujuan tidak ditemukan")

            if (sourceAsset.balance < amount) {
                return Resource.Error("Saldo '${sourceAsset.name}' tidak mencukupi (Sisa: Rp${sourceAsset.balance.toRupiah()})")
            }

            val updatedSourceAsset = sourceAsset.copy(balance = sourceAsset.balance - amount)
            val updatedDestAsset = destAsset.copy(balance = destAsset.balance + amount)

            val txOut = Transaction(
                id = 0,
                fromAssetId = fromAssetId,
                toAssetId = toAssetId,
                categoryId = null,
                type = TransactionType.TRANSFER_OUT,
                categoryName = "Transfer Keluar",
                categoryIcon = "arrow_upward",
                categoryColor = Color.RED,
                fromAssetName = sourceAsset.name,
                amount = amount,
                currency = "IDR",
                convertedAmountIDR = amount,
                exchangeRate = 1.0,
                title = "Transfer ke ${destAsset.name}",
                note = note,
                date = date,
                createdDate = System.currentTimeMillis()
            )

            val txIn = Transaction(
                id = 0,
                fromAssetId = toAssetId,
                toAssetId = fromAssetId,
                categoryId = null,
                type = TransactionType.TRANSFER_IN,
                categoryName = "Transfer Masuk",
                categoryIcon = "arrow_downward",
                categoryColor = Color.GREEN,
                fromAssetName = destAsset.name,
                amount = amount,
                currency = "IDR",
                convertedAmountIDR = amount,
                exchangeRate = 1.0,
                title = "Transfer dari ${sourceAsset.name}",
                note = note,
                date = date,
                createdDate = System.currentTimeMillis()
            )

            transactionRepository.insertTransfer(
                sourceTransaction = txOut,
                destinationTransaction = txIn,
                sourceAsset = updatedSourceAsset,
                destinationAsset = updatedDestAsset
            )

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error("Gagal memproses transfer: ${e.message}")
        }
    }
}