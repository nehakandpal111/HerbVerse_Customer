package com.example.herbverse_customer.domain.usecase.cart

import com.example.herbverse_customer.domain.model.CartItem
import com.example.herbverse_customer.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting cart items
 */
class GetCartItemsUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): List<CartItem> {
        return cartRepository.getCartItems()
    }
    
    fun asFlow(): Flow<List<CartItem>> {
        return cartRepository.getCartItemsFlow()
    }
}