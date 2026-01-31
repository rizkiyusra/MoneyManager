package com.example.moneymanager.presentation.asset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Asset
import com.example.moneymanager.domain.usecase.asset.DeleteAssetUseCase
import com.example.moneymanager.domain.usecase.asset.GetAssetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetListViewModel @Inject constructor(
    private val getAssetsUseCase: GetAssetsUseCase,
    private val deleteAssetUseCase: DeleteAssetUseCase
) : ViewModel() {
    private val _retryTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val assetsState: StateFlow<Resource<List<Asset>>> = _retryTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            getAssetsUseCase()
                .map { Resource.Success(it) as Resource<List<Asset>> }
                .onStart { emit(Resource.Loading()) }
                .catch { emit(Resource.Error(it.message ?: "Unknown Error")) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _deleteState = MutableStateFlow<String?>(null)
    val deleteState = _deleteState.asStateFlow()

    fun getAssets() {
        viewModelScope.launch {
            _retryTrigger.emit(Unit)
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            try {
                deleteAssetUseCase(asset)
                _deleteState.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _deleteState.value = "Gagal menghapus: ${e.message}"
            }
        }
    }

    fun onErrorMessageShown() {
        _deleteState.value = null
    }
}