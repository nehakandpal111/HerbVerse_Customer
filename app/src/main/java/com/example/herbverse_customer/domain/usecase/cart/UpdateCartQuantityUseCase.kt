package com.example.herbverse_customer.domain.usecase.cart

import com.example.herbverse_customer.domain.repository.CartRepository

/**
 * Use case for updating cart item quantity
 */
class UpdateCartQuantityUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, quantity: Int): Boolean {
        return cartRepository.updateCartItem(cartItemId, quantity)
    }
}