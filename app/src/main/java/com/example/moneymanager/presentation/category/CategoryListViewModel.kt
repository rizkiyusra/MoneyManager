package com.example.moneymanager.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.usecase.category.DeleteCategoryUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<Boolean?>(null)
    val selectedFilter: StateFlow<Boolean?> = _selectedFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesState: StateFlow<Resource<List<Category>>> = _selectedFilter
        .flatMapLatest { isIncome ->
            val flow = if (isIncome == null) {
                getCategoriesUseCase()
            } else {
                getCategoriesUseCase(isIncome)
            }
            flow.map { Resource.Success(it) as Resource<List<Category>> }
                .onStart { emit(Resource.Loading()) }
                .catch { emit(Resource.Error(it.message ?: "Gagal memuat kategori")) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading()
        )

    private val _actionState = MutableStateFlow<Resource<String>?>(null)
    val actionState = _actionState.asStateFlow()

    fun onFilterChanged(isIncome: Boolean?) {
        _selectedFilter.value = isIncome
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            _actionState.value = Resource.Loading()
            try {
                deleteCategoryUseCase(category.id)
                _actionState.value = Resource.Success("Kategori '${category.name}' berhasil dihapus")
            } catch (e: Exception) {
                _actionState.value = Resource.Error("Gagal menghapus: ${e.message}")
            }
        }
    }

    fun onActionStateHandled() {
        _actionState.value = null
    }

    fun retry() {
        _selectedFilter.value = _selectedFilter.value
    }
}