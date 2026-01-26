package com.example.moneymanager.presentation.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.usecase.category.AddCategoryUseCase
import com.example.moneymanager.domain.usecase.category.GetCategoryByIdUseCase
import com.example.moneymanager.domain.usecase.category.UpdateCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var categoryName by mutableStateOf("")
        private set

    var isIncomeCategory by mutableStateOf(false)
        private set

    var categoryIcon by mutableStateOf("category")
        private set

    var categoryColor by mutableIntStateOf(Color.Gray.toArgb())
        private set

    private var currentCategoryId: Int? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("categoryId")?.let { id ->
            if (id != -1) {
                viewModelScope.launch {
                    getCategoryByIdUseCase(id)?.let { category ->
                        currentCategoryId = category.id
                        categoryName = category.name
                        isIncomeCategory = category.isIncomeCategory
                        categoryIcon = category.icon
                        categoryColor = category.color
                    }
                }
            }
        }
    }

    fun onNameChange(text: String) {
        categoryName = text
    }

    fun onTypeChange(isIncome: Boolean) {
        isIncomeCategory = isIncome
    }

    fun onIconChange(iconName: String) {
        categoryIcon = iconName
    }

    fun onColorChange(colorInt: Int) {
        categoryColor = colorInt
    }

    fun onSaveCategory() {
        viewModelScope.launch {
            try {
                if (categoryName.isBlank()) {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Nama kategori tidak boleh kosong"))
                    return@launch
                }

                val category = Category(
                    id = currentCategoryId ?: 0,
                    name = categoryName,
                    isIncomeCategory = isIncomeCategory,
                    icon = categoryIcon,
                    color = categoryColor,
                    isSystemCategory = false,
                    isActive = true
                )

                if (currentCategoryId != null) {
                    updateCategoryUseCase(category)
                } else {
                    addCategoryUseCase(category)
                }

                _eventFlow.emit(UiEvent.SaveCategorySuccess)

            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Gagal menyimpan: ${e.message}"))
            }
        }
    }

    sealed class UiEvent {
        object SaveCategorySuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}