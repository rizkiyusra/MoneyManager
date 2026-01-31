package com.example.moneymanager.presentation.recurring

import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.RecurringTransaction

data class RecurringUiState(
    val isLoading: Boolean = true,
    val transactions: List<RecurringTransaction> = emptyList(),
    val assets: List<Asset> = emptyList(),
    val categories: List<Category> = emptyList()
)
