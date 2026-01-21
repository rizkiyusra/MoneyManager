package com.example.moneymanager.presentation.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.TransactionRepository
import com.example.moneymanager.domain.usecase.asset.ReconcileAssetBalanceUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import com.example.moneymanager.domain.usecase.transaction.EditTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val editTransactionUseCase: EditTransactionUseCase,
    private val reconcileAssetBalanceUseCase: ReconcileAssetBalanceUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    private val _transactionToEdit = MutableStateFlow<Transaction?>(null)
    val transactionToEdit: StateFlow<Transaction?> = _transactionToEdit.asStateFlow()
    val currentTransactionId: Int = savedStateHandle.get<Int>("transactionId") ?: -1

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?> = _categories.asStateFlow()
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    init {
        if (currentTransactionId != -1) {
            loadTransaction(currentTransactionId)
        } else {
            loadCategories(isIncomeCategory = false)
        }
    }

    private fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val transaction = repository.getTransactionById(id)
            _transactionToEdit.value = transaction

            transaction?.let { tx ->
                val isIncome = tx.type == TransactionType.INCOME
                loadCategories(isIncome) { categoryList ->
                    val matchingCategory = categoryList.find { it.id == tx.categoryId }
                    _selectedCategory.value = matchingCategory
                }
            }
        }
    }

    fun loadCategories(isIncomeCategory: Boolean, onLoaded: ((List<Category>) -> Unit)? = null) {
        viewModelScope.launch {
            getCategoriesUseCase(isIncomeCategory).collect { list ->
                _categories.value = list

                if (_selectedCategory.value == null && list.isNotEmpty()) {
                    _selectedCategory.value = list.first()
                }

                onLoaded?.invoke(list)
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }

    fun saveTransaction(
        amount: Double,
        note: String,
        type: TransactionType,
        date: Long,
        categoryId: Int,
        fromAssetId: Int,
    ) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val transaction = Transaction(
                id = if (currentTransactionId != -1) currentTransactionId else 0,
                amount = amount,
                note = note,
                type = type,
                date = date,
                categoryName = "",
                categoryIcon = "",
                categoryColor = 0,
                fromAssetName = "",
                categoryId = categoryId,
                fromAssetId = fromAssetId,
                title = note.ifEmpty { "Transaksi" },
                currency = "IDR",
                convertedAmountIDR = amount,
                exchangeRate = 1.0,
                location = null,
                receiptImagePath = null,
                createdDate = System.currentTimeMillis()
            )

            val result = if (currentTransactionId != -1) {
                editTransactionUseCase(transaction)
            } else {
                addTransactionUseCase(transaction)
            }

            if (result is Resource.Success) {
                try {
                    reconcileAssetBalanceUseCase(fromAssetId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            _saveState.value = result
        }
    }

    fun resetState() {
        _saveState.value = null
    }
}