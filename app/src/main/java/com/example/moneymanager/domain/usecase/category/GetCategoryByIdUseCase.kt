package com.example.moneymanager.domain.usecase.category

import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Int): Category? {
        return repository.getCategoryById(id)
    }
}