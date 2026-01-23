package com.example.moneymanager.presentation.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.usecase.asset.AddAssetUseCase
import com.example.moneymanager.domain.usecase.asset.GetAssetByIdUseCase
import com.example.moneymanager.domain.usecase.asset.UpdateAssetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAssetViewModel @Inject constructor(
    private val addAssetUseCase: AddAssetUseCase,
    private val getAssetByIdUseCase: GetAssetByIdUseCase,
    private val updateAssetUseCase: UpdateAssetUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState: StateFlow<Resource<Unit>?> = _saveState.asStateFlow()
    private val _assetDetailState = MutableStateFlow<Resource<Asset>?>(null)
    val assetDetailState: StateFlow<Resource<Asset>?> = _assetDetailState.asStateFlow()

    fun saveAsset(name: String, type: String, initialBalance: Double) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            try {
                val asset = Asset(
                    id = 0,
                    name = name,
                    type = type,
                    balance = 0.0,
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

    fun updateAsset(id: Int, name: String, type: String) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            try {
                val currentAsset = (_assetDetailState.value as? Resource.Success)?.data
                val currentBalance = currentAsset?.balance ?: 0.0

                val updatedAsset = Asset(
                    id = id,
                    name = name,
                    type = type,
                    balance = currentBalance,
                    unit = "IDR",
                    currencySymbol = "Rp",
                    isActive = true
                )

                updateAssetUseCase(updatedAsset)
                _saveState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = Resource.Error(e.message ?: "Gagal mengupdate aset")
            }
        }
    }

    fun getAssetById(id: Int) {
        viewModelScope.launch {
            _assetDetailState.value = Resource.Loading()
            try {
                val asset = getAssetByIdUseCase(id)
                if (asset != null) {
                    _assetDetailState.value = Resource.Success(asset)
                } else {
                    _assetDetailState.value = Resource.Error("Aset tidak ditemukan")
                }
            } catch (e: Exception) {
                _assetDetailState.value = Resource.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun resetState() {
        _saveState.value = null
    }
}