package com.example.moneymanager.presentation.transaction

data class TransactionUiState(
    val isLoading: Boolean = false,
    val amount: String = "",
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val isExpense: Boolean = true,
    val error: String? = null,
    val isSuccess: Boolean = false
)