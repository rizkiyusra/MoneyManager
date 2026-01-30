package com.example.moneymanager.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Budget
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.usecase.budget.AddBudgetUseCase
import com.example.moneymanager.domain.usecase.budget.DeleteBudgetUseCase
import com.example.moneymanager.domain.usecase.budget.GetBudgetsUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val _budgetList = MutableStateFlow<List<Budget>>(emptyList())
    val budgetList: StateFlow<List<Budget>> = _budgetList.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    val currentMonth = _currentMonth.asStateFlow()
    val currentYear = _currentYear.asStateFlow()

    init {
        loadBudgets()
        loadCategories()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            getBudgetsUseCase(_currentMonth.value, _currentYear.value).collectLatest {
                _budgetList.value = it
            }
        }
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
        cal.add(Calendar.MONTH, increment)

        _currentMonth.value = cal.get(Calendar.MONTH)
        _currentYear.value = cal.get(Calendar.YEAR)

        loadBudgets()
    }

    fun addBudget(categoryId: Int, name: String, limit: Double) {
        viewModelScope.launch {
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
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            deleteBudgetUseCase(budget)
        }
    }
}