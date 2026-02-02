package com.example.moneymanager.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionFilter
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.AssetRepository
import com.example.moneymanager.domain.repository.CategoryRepository
import com.example.moneymanager.domain.usecase.transaction.GetFilteredTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase,
    categoryRepository: CategoryRepository,
    assetRepository: AssetRepository
) : ViewModel() {
    private val _filterState = MutableStateFlow(TransactionFilter())
    val filterState = _filterState.asStateFlow()

    val categories = categoryRepository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets = assetRepository.getAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<Transaction>> = _filterState
        .flatMapLatest { filter ->
            getFilteredTransactionsUseCase(filter)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDateRangeChanged(start: Long?, end: Long?) {
        _filterState.update { it.copy(startDate = start, endDate = end) }
    }

    fun onTypeChanged(type: TransactionType?) {
        _filterState.update { it.copy(transactionType = type) }
    }

    fun onCategoryChanged(categoryId: Int?) {
        _filterState.update { it.copy(categoryId = categoryId) }
    }

    fun onAssetChanged(assetId: Int?) {
        _filterState.update { it.copy(assetId = assetId) }
    }

    fun onResetFilter() {
        _filterState.value = TransactionFilter()
    }
}