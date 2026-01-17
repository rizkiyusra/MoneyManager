package com.example.moneymanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.presentation.ui.state.DashboardUiState
import com.example.moneymanager.presentation.ui.state.TransactionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    // TODO: Inject repositories when created
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                delay(1000)

                val mockTransactions = listOf(
                    TransactionUiModel(
                        id = 1,
                        title = "Makan Siang",
                        category = "Food & Drinks",
                        amount = "-Rp 45,000",
                        isExpense = true,
                        date = "Today"
                    ),
                    TransactionUiModel(
                        id = 2,
                        title = "Gaji Bulanan",
                        category = "Salary",
                        amount = "+Rp 8,000,000",
                        isExpense = false,
                        date = "2 days ago"
                    ),
                    TransactionUiModel(
                        id = 3,
                        title = "Transfer to Savings",
                        category = "Transfer",
                        amount = "-Rp 1,000,000",
                        isExpense = true,
                        date = "3 days ago"
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalBalance = 15_750_000.0,
                    monthlyIncome = 8_500_000.0,
                    monthlyExpense = 4_250_000.0,
                    recentTransactions = mockTransactions
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun retryLoading() {
        loadDashboardData()
    }
}