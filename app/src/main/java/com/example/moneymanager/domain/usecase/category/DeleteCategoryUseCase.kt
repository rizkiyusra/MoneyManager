package com.example.moneymanager.domain.usecase.category

import com.example.moneymanager.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteCategory(id)
    }
}