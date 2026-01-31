package com.example.moneymanager.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.usecase.dashboard.GetDashboardDataUseCase
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import com.example.moneymanager.domain.usecase.transaction.DeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {
    private val _retryTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = _retryTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            getDashboardDataUseCase()
                .map { data ->
                    DashboardUiState(
                        isLoading = false,
                        error = null,
                        totalBalance = data.totalBalance,
                        monthlyIncome = data.monthlyIncome,
                        monthlyExpense = data.monthlyExpense,
                        recentTransactions = data.recentTransactions
                    )
                }
                .onStart {
                    emit(DashboardUiState(isLoading = true))
                }
                .catch { e ->
                    emit(DashboardUiState(isLoading = false, error = e.message ?: "Terjadi kesalahan"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )

    private val _eventFlow = MutableSharedFlow<DashboardEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var recentlyDeletedTransaction: Transaction? = null

    fun retryLoading() {
        viewModelScope.launch {
            _retryTrigger.emit(Unit)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                recentlyDeletedTransaction = transaction
                deleteTransactionUseCase(transaction)

                _eventFlow.emit(DashboardEvent.ShowUndoSnackbar("Transaksi dihapus"))
            } catch (e: Exception) {
                _eventFlow.emit(DashboardEvent.ShowError("Gagal menghapus: ${e.message}"))
            }
        }
    }

    fun restoreTransaction() {
        viewModelScope.launch {
            recentlyDeletedTransaction?.let { transaction ->
                try {
                    addTransactionUseCase(transaction)
                    recentlyDeletedTransaction = null
                } catch (_: Exception) {
                    _eventFlow.emit(DashboardEvent.ShowError("Gagal mengembalikan data"))
                }
            }
        }
    }

    sealed class DashboardEvent {
        data class ShowUndoSnackbar(val message: String) : DashboardEvent()
        data class ShowError(val message: String) : DashboardEvent()
    }
}