package com.example.moneymanager.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import com.example.moneymanager.domain.usecase.transaction.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

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
                title = note.ifEmpty { "Transaksi Baru" },
                currency = "IDR",
                convertedAmountIDR = amount,
                exchangeRate = 1.0,
                location = null,
                receiptImagePath = null,
                createdDate = System.currentTimeMillis()
            )

            val result = addTransactionUseCase(transaction)
            _saveState.value = result
        }
    }

    fun resetState() {
        _saveState.value = null
    }
}