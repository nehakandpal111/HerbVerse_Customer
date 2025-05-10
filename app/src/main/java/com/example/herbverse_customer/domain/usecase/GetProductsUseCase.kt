package com.example.herbverse_customer.domain.usecase

import com.example.herbverse_customer.domain.model.Product
import com.example.herbverse_customer.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting products
 */
class GetProductsUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): List<Product> {
        return productRepository.getProducts()
    }
    
    fun asFlow(): Flow<List<Product>> {
        return productRepository.getProductsFlow()
    }
}