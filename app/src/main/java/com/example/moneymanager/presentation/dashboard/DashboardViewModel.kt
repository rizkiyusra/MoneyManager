package com.example.moneymanager.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.usecase.dashboard.GetDashboardDataUseCase
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import com.example.moneymanager.domain.usecase.transaction.DeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun retryLoading() {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getDashboardDataUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Terjadi kesalahan tidak diketahui"
                    )
                }
                .collect { dashboardData ->
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        error = null,
                        totalBalance = dashboardData.totalBalance,
                        monthlyIncome = dashboardData.monthlyIncome,
                        monthlyExpense = dashboardData.monthlyExpense,
                        recentTransactions = dashboardData.recentTransactions
                    )
                }
        }
    }

    private var recentlyDeletedTransaction: Transaction? = null

    fun deleteTransaction(transaction: Transaction)  {
        viewModelScope.launch {
            recentlyDeletedTransaction = transaction
            deleteTransactionUseCase(transaction)
        }
    }

    fun restoreTransaction() {
        viewModelScope.launch {
            recentlyDeletedTransaction?.let { transaction ->
                addTransactionUseCase(transaction)
                recentlyDeletedTransaction = null
            }
        }
    }
}