package com.example.moneymanager.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.usecase.budget.AddBudgetUseCase
import com.example.moneymanager.domain.usecase.budget.DeleteBudgetUseCase
import com.example.moneymanager.domain.usecase.budget.GetBudgetsUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentMonth = _currentMonth.asStateFlow()
    val currentYear = _currentYear.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val budgetList: StateFlow<List<Budget>> = combine(_currentMonth, _currentYear) { month, year ->
        Pair(month, year)
    }.flatMapLatest { (month, year) ->
        getBudgetsUseCase(month, year)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _actionState = MutableStateFlow<Resource<String>?>(null)
    val actionState = _actionState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase(false).collect {
                _categories.value = it
            }
        }
    }

    fun changeMonth(increment: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, _currentYear.value)
        cal.set(Calendar.MONTH, _currentMonth.value)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        cal.add(Calendar.MONTH, increment)

        _currentMonth.value = cal.get(Calendar.MONTH)
        _currentYear.value = cal.get(Calendar.YEAR)
    }

    fun addBudget(categoryId: Int, name: String, limit: Double) {
        viewModelScope.launch {
            _actionState.value = Resource.Loading()
            try {
                val newBudget = Budget(
                    categoryId = categoryId,
                    name = name,
                    limit = limit,
                    period = "MONTHLY",
                    month = _currentMonth.value,
                    year = _currentYear.value,
                    isActive = true,
                    alertThreshold = 0.8
                )
                addBudgetUseCase(newBudget)
                _actionState.value = Resource.Success("Budget berhasil ditambahkan")
            } catch (e: Exception) {
                _actionState.value = Resource.Error(e.message ?: "Gagal menambah budget")
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                deleteBudgetUseCase(budget)
                _actionState.value = Resource.Success("Budget dihapus")
            } catch (e: Exception) {
                _actionState.value = Resource.Error("Gagal menghapus: ${e.message}")
            }
        }
    }

    fun onActionStateHandled() {
        _actionState.value = null
    }
}