package com.example.herbverse_customer.data.repository

import com.example.herbverse_customer.data.FirestoreRepository
import com.example.herbverse_customer.domain.model.Product
import com.example.herbverse_customer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of the ProductRepository
 */
class ProductRepositoryImpl(
    private val firestoreRepository: FirestoreRepository
) : ProductRepository {
    
    override suspend fun getProducts(): List<Product> {
        return firestoreRepository.getProducts().map { it.toDomainModel() }
    }
    
    override fun getProductsFlow(): Flow<List<Product>> {
        return firestoreRepository.getProductsFlow().map { products ->
            products.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getProductById(id: String): Product? {
        // This is a simplified implementation - in a real app, you would fetch from the database
        return firestoreRepository.getProducts()
            .firstOrNull { it.id == id }
            ?.toDomainModel()
    }
    
    override suspend fun getProductsByCategory(categoryId: String): List<Product> {
        return firestoreRepository.getProductsByCategory(categoryId)
            .map { it.toDomainModel() }
    }
    
    override suspend fun searchProducts(query: String): List<Product> {
        // Simplified implementation - in a real app, you would use a search API
        return firestoreRepository.getProducts()
            .filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.shortDescription.contains(query, ignoreCase = true) 
            }
            .map { it.toDomainModel() }
    }
    
    override suspend fun getFavoriteProducts(): List<Product> {
        // In a real app, you would fetch this from a local database
        return emptyList()
    }
    
    override suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Boolean {
        // In a real app, you would update a local database
        return true
    }
    
    override suspend fun updateProductRating(productId: String, rating: Float): Boolean {
        // In a real app, you would update this in the backend
        return true
    }
    
    // Extension function to convert data model to domain model
    private fun com.example.herbverse_customer.data.model.Product.toDomainModel(): Product {
        return Product(
            id = this.id,
            name = this.name,
            shortDescription = this.shortDescription,
            fullDescription = this.fullDescription,
            price = this.price,
            stock = this.stock,
            categoryId = this.categoryId,
            imageUrl = this.imageUrl ?: "",
            // These fields would come from the backend in a real app
            rating = 4.5f,
            reviewCount = 10,
            vendorId = "vendor_1"
        )
    }
}