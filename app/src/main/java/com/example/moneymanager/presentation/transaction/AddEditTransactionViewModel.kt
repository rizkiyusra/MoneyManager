package com.example.moneymanager.presentation.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.repository.TransactionRepository
import com.example.moneymanager.domain.usecase.asset.GetAssetsUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import com.example.moneymanager.domain.usecase.transaction.AddTransferUseCase
import com.example.moneymanager.domain.usecase.transaction.DeleteTransactionUseCase
import com.example.moneymanager.domain.usecase.transaction.EditTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val editTransactionUseCase: EditTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addTransferUseCase: AddTransferUseCase,
    private val getAssetsUseCase: GetAssetsUseCase,
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    private val _transactionToEdit = MutableStateFlow<Transaction?>(null)
    val transactionToEdit: StateFlow<Transaction?> = _transactionToEdit.asStateFlow()
    val currentTransactionId: Int = savedStateHandle.get<Int>("transactionId")?.takeIf { it != -1 } ?: 0

    private val _categories = MutableStateFlow<List<Category>?>(emptyList())
    val categories: StateFlow<List<Category>?> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _assets = MutableStateFlow<List<Asset>?>(emptyList())
    val assets: StateFlow<List<Asset>?> = _assets.asStateFlow()

    init {
        loadAssets()

        if (currentTransactionId != 0) {
            loadTransaction(currentTransactionId)
        } else {
            loadCategories(isIncomeCategory = false)
        }
    }

    private fun loadAssets() {
        viewModelScope.launch {
            getAssetsUseCase().collect { list ->
                _assets.value = list
            }
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
        if (onLoaded == null) {
            _selectedCategory.value = null
        }

        viewModelScope.launch {
            getCategoriesUseCase(isIncomeCategory).collect { list ->
                _categories.value = list
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
        toAssetId: Int? = null
    ) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()

            if (type == TransactionType.TRANSFER_OUT) {

                if (toAssetId == null) {
                    _saveState.value = Resource.Error("Dompet Tujuan harus dipilih!")
                    return@launch
                }

                val result = addTransferUseCase(
                    amount = amount,
                    note = note,
                    date = date,
                    fromAssetId = fromAssetId,
                    toAssetId = toAssetId
                )
                _saveState.value = result

            } else {
                if (categoryId == 0) {
                    _saveState.value = Resource.Error("Silakan pilih kategori!")
                    return@launch
                }

                val currentCategory = _categories.value?.find { it.id == categoryId }
                val currentAsset = _assets.value?.find { it.id == fromAssetId }

                val transaction = Transaction(
                    id = currentTransactionId,
                    amount = amount,
                    note = note,
                    type = type,
                    date = date,
                    categoryName = currentCategory?.name ?: "",
                    categoryIcon = currentCategory?.icon ?: "",
                    categoryColor = currentCategory?.color ?: 0,
                    fromAssetId = fromAssetId,
                    fromAssetName = currentAsset?.name ?: "",
                    categoryId = categoryId,
                    title = note.ifEmpty { currentCategory?.name ?: "Transaksi" },
                    currency = "IDR",
                    convertedAmountIDR = amount,
                    exchangeRate = 1.0,
                    location = null,
                    receiptImagePath = null,
                    createdDate = System.currentTimeMillis()
                )

                val result = if (currentTransactionId != 0) {
                    editTransactionUseCase(transaction)
                } else {
                    addTransactionUseCase(transaction)
                }

                _saveState.value = result
            }
        }
    }

    fun deleteTransaction() {
        val transaction = _transactionToEdit.value
        if (transaction != null) {
            viewModelScope.launch {
                _saveState.value = Resource.Loading()
                val result = deleteTransactionUseCase(transaction)
                _saveState.value = result
            }
        }
    }

    fun resetState() {
        _saveState.value = null
    }
}