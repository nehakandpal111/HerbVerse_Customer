package com.example.herbverse_customer.domain.repository

import com.example.herbverse_customer.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product operations
 */
interface ProductRepository {
    suspend fun getProducts(): List<Product>
    fun getProductsFlow(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun getProductsByCategory(categoryId: String): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getFavoriteProducts(): List<Product>
    suspend fun toggleFavorite(productId: String, isFavorite: Boolean): Boolean
    suspend fun updateProductRating(productId: String, rating: Float): Boolean
}