package com.example.moneymanager.presentation.dashboard

import com.example.moneymanager.domain.model.Transaction

data class DashboardUiState(
    val isLoading: Boolean = false,
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)