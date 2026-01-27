package com.example.moneymanager.domain.usecase.transaction

import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.TransactionRepository
import com.example.moneymanager.domain.usecase.asset.ReconcileAssetBalanceUseCase
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val reconcileAssetBalanceUseCase: ReconcileAssetBalanceUseCase
) {
    suspend operator fun invoke(transaction: Transaction): Resource<Unit> {
        return try {
            repository.deleteTransaction(transaction)
            reconcileAssetBalanceUseCase(transaction.fromAssetId)

            if (transaction.type == TransactionType.TRANSFER_OUT || transaction.type == TransactionType.TRANSFER_IN) {
                transaction.toAssetId?.let { targetId ->
                    reconcileAssetBalanceUseCase(targetId)
                }
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus: ${e.message}")
        }
    }
}