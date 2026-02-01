package com.example.moneymanager.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.usecase.transaction.SearchTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTransactionsUseCase: SearchTransactionsUseCase
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Transaction>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { currentQuery ->
            if (currentQuery.isBlank()) {
                flowOf(emptyList())
            } else {
                searchTransactionsUseCase(currentQuery)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}