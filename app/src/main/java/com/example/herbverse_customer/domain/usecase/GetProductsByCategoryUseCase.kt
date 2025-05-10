package com.example.herbverse_customer.domain.usecase

import com.example.herbverse_customer.domain.model.Product
import com.example.herbverse_customer.domain.repository.ProductRepository

/**
 * Use case for getting products by category
 */
class GetProductsByCategoryUseCase(private val productRepository: ProductRepository) {
    suspend operator fun invoke(categoryId: String): List<Product> {
        return productRepository.getProductsByCategory(categoryId)
    }
}