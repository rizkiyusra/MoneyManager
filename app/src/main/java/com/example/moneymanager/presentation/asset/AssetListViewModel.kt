package com.example.moneymanager.presentation.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.usecase.asset.DeleteAssetUseCase
import com.example.moneymanager.domain.usecase.asset.GetAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetListViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val deleteAssetUseCase: DeleteAssetUseCase
) : ViewModel() {

    private val _assetsState = MutableStateFlow<Resource<List<Asset>>>(Resource.Loading())
    val assetsState: StateFlow<Resource<List<Asset>>> = _assetsState.asStateFlow()

    init {
        getAssets()
    }

    private fun getAssets() {
        viewModelScope.launch {
            getAssetsUseCase()
                .catch { e -> _assetsState.value = Resource.Error(e.message ?: "Unknown Error") }
                .collect { list -> _assetsState.value = Resource.Success(list) }
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            try {
                deleteAssetUseCase(asset)
            } catch (e: Exception) {
                e.printStackTrace()
                _assetsState.value = Resource.Error("Gagal menghapus: ${e.message}")            }
        }
    }
}