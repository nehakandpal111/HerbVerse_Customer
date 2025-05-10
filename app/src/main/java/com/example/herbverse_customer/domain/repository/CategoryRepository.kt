package com.example.herbverse_customer.domain.repository

import com.example.herbverse_customer.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for category operations
 */
interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    fun getCategoriesFlow(): Flow<List<Category>>
    suspend fun getCategoryById(id: String): Category?
}