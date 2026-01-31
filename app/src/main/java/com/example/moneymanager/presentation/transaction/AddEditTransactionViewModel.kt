package com.example.moneymanager.presentation.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.extension.cleanToDouble
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val editTransactionUseCase: EditTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addTransferUseCase: AddTransferUseCase,
    getAssetsUseCase: GetAssetsUseCase,
    private val repository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var amount by mutableStateOf("")
        private set
    var note by mutableStateOf("")
        private set
    var date by mutableLongStateOf(System.currentTimeMillis())
        private set
    var type by mutableStateOf(TransactionType.EXPENSE)
        private set

    var selectedCategoryId by mutableStateOf<Int?>(null)
        private set
    var selectedFromAssetId by mutableStateOf<Int?>(null)
        private set
    var selectedToAssetId by mutableStateOf<Int?>(null)
        private set

    val assets: StateFlow<List<Asset>> = getAssetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<Category>> = snapshotFlow { type }
        .flatMapLatest { transactionType ->
            val isIncome = transactionType == TransactionType.INCOME
            getCategoriesUseCase(isIncome)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val currentTransactionId: Int = savedStateHandle.get<Int>("transactionId")?.takeIf { it != -1 } ?: 0
    private var currentTransaction: Transaction? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (currentTransactionId != 0) {
            loadTransaction(currentTransactionId)
        }
    }

    private fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val transaction = repository.getTransactionById(id)
            currentTransaction = transaction
            transaction?.let { tx ->
                amount = if (tx.amount == 0.0) "" else tx.amount.toLong().toString()
                note = tx.note ?: ""
                date = tx.date
                type = tx.type
                selectedCategoryId = tx.categoryId
                selectedFromAssetId = tx.fromAssetId
            }
        }
    }

    fun onAmountChange(value: String) { amount = value }
    fun onNoteChange(value: String) { note = value }
    fun onDateChange(value: Long) { date = value }
    fun onTypeChange(newType: TransactionType) {
        type = newType
    }
    fun onCategorySelect(id: Int) { selectedCategoryId = id }
    fun onFromAssetSelect(id: Int) { selectedFromAssetId = id }
    fun onToAssetSelect(id: Int) { selectedToAssetId = id }

    fun onSaveTransaction() {
        viewModelScope.launch {
            val amountVal = amount.cleanToDouble()
            if (amountVal <= 0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Masukkan jumlah yang valid"))
                return@launch
            }
            if (selectedFromAssetId == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Pilih dompet asal"))
                return@launch
            }

            try {
                if (type == TransactionType.TRANSFER_OUT) {
                    if (selectedToAssetId == null) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Pilih dompet tujuan"))
                        return@launch
                    }
                    if (selectedFromAssetId == selectedToAssetId) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Dompet tujuan tidak boleh sama"))
                        return@launch
                    }

                    addTransferUseCase(
                        amount = amountVal,
                        note = note,
                        date = date,
                        fromAssetId = selectedFromAssetId!!,
                        toAssetId = selectedToAssetId!!
                    )

                } else {
                    if (selectedCategoryId == null || selectedCategoryId == 0) {
                        _eventFlow.emit(UiEvent.ShowSnackbar("Pilih kategori"))
                        return@launch
                    }

                    val category = categories.value.find { it.id == selectedCategoryId }
                    val asset = assets.value.find { it.id == selectedFromAssetId }

                    val transaction = Transaction(
                        id = currentTransactionId,
                        amount = amountVal,
                        note = note,
                        type = type,
                        date = date,
                        categoryName = category?.name ?: "",
                        categoryIcon = category?.icon ?: "",
                        categoryColor = category?.color ?: 0,
                        fromAssetId = selectedFromAssetId!!,
                        fromAssetName = asset?.name ?: "",
                        categoryId = selectedCategoryId!!,
                        title = note.ifEmpty { category?.name ?: "Transaksi" },
                        currency = "IDR",
                        convertedAmountIDR = amountVal,
                        exchangeRate = 1.0,
                        location = null,
                        receiptImagePath = null,
                        createdDate = if (currentTransactionId == 0) System.currentTimeMillis() else currentTransaction!!.createdDate
                    )

                    if (currentTransactionId != 0) {
                        editTransactionUseCase(transaction)
                    } else {
                        addTransactionUseCase(transaction)
                    }
                }

                _eventFlow.emit(UiEvent.SaveSuccess)

            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    fun onDeleteTransaction() {
        viewModelScope.launch {
            currentTransaction?.let {
                deleteTransactionUseCase(it)
                _eventFlow.emit(UiEvent.SaveSuccess)
            }
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}