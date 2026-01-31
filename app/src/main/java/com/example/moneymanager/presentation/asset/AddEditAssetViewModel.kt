package com.example.moneymanager.presentation.asset

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.extension.cleanToDouble
import com.example.moneymanager.common.extension.formatToThousandSeparator
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.usecase.asset.AddAssetUseCase
import com.example.moneymanager.domain.usecase.asset.GetAssetByIdUseCase
import com.example.moneymanager.domain.usecase.asset.UpdateAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAssetViewModel @Inject constructor(
    private val addAssetUseCase: AddAssetUseCase,
    private val getAssetByIdUseCase: GetAssetByIdUseCase,
    private val updateAssetUseCase: UpdateAssetUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var assetName by mutableStateOf("")
        private set

    var assetType by mutableStateOf("CASH")
        private set

    var currentBalance by mutableStateOf("")
        private set

    private var currentAssetId: Int? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("assetId")?.let { id ->
            if (id != -1) {
                loadAsset(id)
            }
        }
    }

    private fun loadAsset(id: Int) {
        viewModelScope.launch {
            val asset = getAssetByIdUseCase(id)
            if (asset != null) {
                currentAssetId = asset.id
                assetName = asset.name
                assetType = asset.type
                currentBalance = asset.balance.toString().formatToThousandSeparator()
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar("Aset tidak ditemukan"))
            }
        }
    }


    fun onNameChange(text: String) {
        assetName = text
    }

    fun onTypeChange(type: String) {
        assetType = type
    }

    fun onBalanceChange(text: String) {
        currentBalance = text.formatToThousandSeparator()
    }

    fun onSaveAsset() {
        viewModelScope.launch {
            if (assetName.isBlank()) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Nama aset tidak boleh kosong"))
                return@launch
            }

            try {
                val balanceValue = currentBalance.cleanToDouble()

                if (currentAssetId != null) {
                    val updatedAsset = Asset(
                        id = currentAssetId!!,
                        name = assetName,
                        type = assetType,
                        balance = balanceValue,
                        unit = "IDR",
                        currencySymbol = "Rp",
                        isActive = true
                    )
                    updateAssetUseCase(updatedAsset)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Aset berhasil diperbarui"))
                } else {
                    val newAsset = Asset(
                        id = 0,
                        name = assetName,
                        type = assetType,
                        balance = 0.0,
                        unit = "IDR",
                        currencySymbol = "Rp",
                        isActive = true
                    )
                    addAssetUseCase(newAsset, balanceValue)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Aset berhasil ditambahkan"))
                }

                _eventFlow.emit(UiEvent.SaveSuccess)

            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    sealed class UiEvent {
        data object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}