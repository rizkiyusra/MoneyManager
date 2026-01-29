package com.example.moneymanager.presentation.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.domain.usecase.asset.GetAssetsUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import com.example.moneymanager.domain.usecase.recurring.AddRecurringUseCase
import com.example.moneymanager.domain.usecase.recurring.DeleteRecurringUseCase
import com.example.moneymanager.domain.usecase.recurring.GetRecurringTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val addRecurringUseCase: AddRecurringUseCase,
    private val getRecurringUseCase: GetRecurringTransactionsUseCase,
    private val deleteRecurringUseCase: DeleteRecurringUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAssetsUseCase: GetAssetsUseCase
) : ViewModel() {

    private val _recurringList = MutableStateFlow<List<RecurringTransaction>>(emptyList())
    val recurringList: StateFlow<List<RecurringTransaction>> = _recurringList.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> = _assets.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getRecurringUseCase().collect {
                _recurringList.value = it
            }
        }
        viewModelScope.launch {
            getCategoriesUseCase(false).collect {
                _categories.value = it
            }
        }
        viewModelScope.launch {
            getAssetsUseCase().collect {
                _assets.value = it
            }
        }
    }

    fun loadCategories(isIncome: Boolean) {
        viewModelScope.launch {
            getCategoriesUseCase(isIncome).collect {
                _categories.value = it
            }
        }
    }

    fun saveRecurringTransaction(
        amount: Double,
        note: String,
        isIncome: Boolean,
        categoryId: Int,
        assetId: Int,
        frequency: String
    ) {
        viewModelScope.launch {
            val nextRun = calculateNextRun(System.currentTimeMillis(), frequency)

            val recurring = RecurringTransaction(
                amount = amount,
                note = note,
                isIncome = isIncome,
                categoryId = categoryId,
                assetId = assetId,
                frequency = frequency,
                nextRunDate = nextRun,
                createdDate = System.currentTimeMillis()
            )
            addRecurringUseCase(recurring)
        }
    }

    fun deleteRecurring(item: RecurringTransaction) {
        viewModelScope.launch {
            deleteRecurringUseCase(item)
        }
    }

    private fun calculateNextRun(currentTime: Long, frequency: String): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime

        when (frequency) {
            "DAILY" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(Calendar.MONTH, 1)
            "YEARLY" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }
}