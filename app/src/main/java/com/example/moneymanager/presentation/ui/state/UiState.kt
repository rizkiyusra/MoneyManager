package com.example.moneymanager.presentation.ui.state

// Generic Result wrapper for API calls
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Dashboard specific UI state
data class DashboardUiState(
    val isLoading: Boolean = false,
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val error: String? = null
)

// UI Models (simplified for now)
data class TransactionUiModel(
    val id: Int,
    val title: String,
    val category: String,
    val amount: String,
    val isExpense: Boolean,
    val date: String
)

data class AssetUiModel(
    val id: Int,
    val name: String,
    val type: String,
    val balance: String,
    val convertedBalance: String
)