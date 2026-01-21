package com.example.moneymanager.presentation.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.usecase.asset.AddAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAssetViewModel @Inject constructor(
    private val addAssetUseCase: AddAssetUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()

    fun saveAsset(name: String, type: String, initialBalance: Double) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            try {
                val asset = Asset(
                    id = 0,
                    name = name,
                    type = type,
                    balance = initialBalance,
                    unit = "IDR",
                    currencySymbol = "Rp",
                    isActive = true
                )

                addAssetUseCase(asset, initialBalance)

                _saveState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = Resource.Error(e.message ?: "Gagal menyimpan aset")
            }
        }
    }

    fun resetState() {
        _saveState.value = null
    }
}