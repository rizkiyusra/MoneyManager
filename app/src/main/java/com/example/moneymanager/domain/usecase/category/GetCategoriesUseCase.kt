package com.example.moneymanager.domain.usecase.category

import com.example.moneymanager.domain.model.Category
import com.example.moneymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(isIncomeCategory: Boolean): Flow<List<Category>> {
        return repository.getCategories()
            .map { list ->
                list.filter { category ->
                    category.isIncomeCategory == isIncomeCategory && category.isActive
                }
            }
    }
}