package com.example.moneymanager.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.common.state.Resource
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.usecase.category.DeleteCategoryUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>>(Resource.Loading())
    val categoriesState: StateFlow<Resource<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedFilter = MutableStateFlow<Boolean?>(null)
    val selectedFilter: StateFlow<Boolean?> = _selectedFilter.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadCategories()
    }

    fun onFilterChanged(isIncome: Boolean?) {
        _selectedFilter.value = isIncome
        loadCategories()
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                deleteCategoryUseCase(category.id)
            } catch (_: Exception) {
            }
        }
    }

    private fun loadCategories() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            val filter = _selectedFilter.value

            val flow = if (filter == null) {
                getCategoriesUseCase()
            } else {
                getCategoriesUseCase(filter)
            }

            flow
                .onStart {
                    _categoriesState.value = Resource.Loading()
                }
                .catch { e ->
                    _categoriesState.value = Resource.Error(e.message ?: "Gagal memuat kategori")
                }
                .collect { list ->
                    _categoriesState.value = Resource.Success(list)
                }
        }
    }
}