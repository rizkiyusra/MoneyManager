package com.example.moneymanager.presentation.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.MonthlyCategoryTrend
import com.example.moneymanager.domain.usecase.report.GetCategoryTransactionsUseCase
import com.example.moneymanager.domain.usecase.report.GetCategoryTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryDetailUiState(
    val isLoading: Boolean = true,
    val monthlyTrend: List<MonthlyCategoryTrend> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val totalExpense: Double = 0.0
)

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCategoryTrendUseCase: GetCategoryTrendUseCase,
    getCategoryTransactionsUseCase: GetCategoryTransactionsUseCase
) : ViewModel() {
    private val categoryId: Int = checkNotNull(savedStateHandle["categoryId"]) {
        "CategoryId wajib ada"
    }

    val uiState: StateFlow<CategoryDetailUiState> = combine(
        getCategoryTrendUseCase(categoryId),
        getCategoryTransactionsUseCase(categoryId)
    ) { trend, transactions ->

        CategoryDetailUiState(
            isLoading = false,
            monthlyTrend = trend,
            transactions = transactions,
            totalExpense = transactions.sumOf { it.amount }
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryDetailUiState(isLoading = true)
    )
}