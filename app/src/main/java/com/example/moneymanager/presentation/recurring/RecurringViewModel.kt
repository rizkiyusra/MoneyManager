package com.example.moneymanager.presentation.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.RecurringTransaction
import com.example.moneymanager.domain.usecase.asset.GetAssetsUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import com.example.moneymanager.domain.usecase.recurring.AddRecurringUseCase
import com.example.moneymanager.domain.usecase.recurring.DeleteRecurringUseCase
import com.example.moneymanager.domain.usecase.recurring.GetRecurringTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val addRecurringUseCase: AddRecurringUseCase,
    getRecurringUseCase: GetRecurringTransactionsUseCase,
    private val deleteRecurringUseCase: DeleteRecurringUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    getAssetsUseCase: GetAssetsUseCase
) : ViewModel() {

    private val _isIncomeType = MutableStateFlow(false)
    val isIncomeType = _isIncomeType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categoriesFlow = _isIncomeType.flatMapLatest { isIncome ->
        getCategoriesUseCase(isIncome)
    }

    val uiState: StateFlow<RecurringUiState> = combine(
        getRecurringUseCase(),
        getAssetsUseCase(),
        categoriesFlow
    ) { transactions, assets, categories ->
        RecurringUiState(
            isLoading = false,
            transactions = transactions,
            assets = assets,
            categories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RecurringUiState(isLoading = true)
    )

    private val _actionState = MutableStateFlow<Resource<String>?>(null)
    val actionState = _actionState.asStateFlow()

    fun onTypeChanged(isIncome: Boolean) {
        _isIncomeType.value = isIncome
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
            if (amount <= 0) {
                _actionState.value = Resource.Error("Jumlah harus lebih dari 0")
                return@launch
            }

            _actionState.value = Resource.Loading()
            try {
                val nextRun = calculateNextRun(System.currentTimeMillis(), frequency)

                val recurring = RecurringTransaction(
                    id = 0,
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
                _actionState.value = Resource.Success("Jadwal transaksi berhasil disimpan")
            } catch (e: Exception) {
                _actionState.value = Resource.Error(e.message ?: "Gagal menyimpan jadwal")
            }
        }
    }

    fun deleteRecurring(item: RecurringTransaction) {
        viewModelScope.launch {
            try {
                deleteRecurringUseCase(item)
                _actionState.value = Resource.Success("Jadwal dihapus")
            } catch (e: Exception) {
                _actionState.value = Resource.Error("Gagal menghapus: ${e.message}")
            }
        }
    }

    fun onActionStateHandled() {
        _actionState.value = null
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