package com.example.moneymanager.domain.usecase.category

import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        if (category.name.isBlank()) {
            throw IllegalArgumentException("Nama kategori tidak boleh kosong")
        }
        repository.updateCategory(category)
    }
}