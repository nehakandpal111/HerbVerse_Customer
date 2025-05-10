package com.example.herbverse_customer.data.repository

import com.example.herbverse_customer.data.FirestoreRepository
import com.example.herbverse_customer.domain.model.Category
import com.example.herbverse_customer.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of the CategoryRepository
 */
class CategoryRepositoryImpl(
    private val firestoreRepository: FirestoreRepository
) : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return firestoreRepository.getCategories().map { it.toDomainModel() }
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        // In a real app, this would be a flow from the database
        return flow { 
            emit(getCategories()) 
        }
    }

    override suspend fun getCategoryById(id: String): Category? {
        return firestoreRepository.getCategories()
            .firstOrNull { it.id == id }
            ?.toDomainModel()
    }
    
    // Extension function to convert data model to domain model
    private fun com.example.herbverse_customer.data.model.Category.toDomainModel(): Category {
        return Category(
            id = this.id,
            name = this.name,
            // These fields would come from the backend in a real app
            description = "A collection of high-quality ${this.name.lowercase()}",
            imageUrl = "",  // In a real app, this would come from the backend
            productCount = 5  // In a real app, this would be calculated
        )
    }
}