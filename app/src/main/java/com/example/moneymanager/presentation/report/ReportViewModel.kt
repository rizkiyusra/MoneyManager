package com.example.moneymanager.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.utils.DateUtils
import com.example.moneymanager.domain.model.CategoryExpense
import com.example.moneymanager.domain.model.DailySummary
import com.example.moneymanager.domain.usecase.report.GetCategoryExpensesUseCase
import com.example.moneymanager.domain.usecase.report.GetFinancialTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getCategoryExpensesUseCase: GetCategoryExpensesUseCase,
    private val getFinancialTrendUseCase: GetFinancialTrendUseCase
) : ViewModel() {
    private val _dateRange = MutableStateFlow(DateUtils.getCurrentMonthRange())
    val dateRange = _dateRange.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryExpenses: StateFlow<List<CategoryExpense>> = _dateRange
        .flatMapLatest { (start, end) ->
            getCategoryExpensesUseCase(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val financialTrend: StateFlow<List<DailySummary>> = _dateRange
        .flatMapLatest { (start, end) ->
            getFinancialTrendUseCase(start, end)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun previousMonth() {
        val currentStart = _dateRange.value.first
        val newRange = DateUtils.getPreviousMonthRange(currentStart)
        _dateRange.value = newRange
    }

    fun nextMonth() {
        val currentStart = _dateRange.value.first
        val newRange = DateUtils.getNextMonthRange(currentStart)
        _dateRange.value = newRange
    }
}