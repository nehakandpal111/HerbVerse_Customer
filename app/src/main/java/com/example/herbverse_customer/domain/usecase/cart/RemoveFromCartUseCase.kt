package com.example.herbverse_customer.domain.usecase.cart

import com.example.herbverse_customer.domain.repository.CartRepository

/**
 * Use case for removing items from cart
 */
class RemoveFromCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartItemId: String): Boolean {
        return cartRepository.removeFromCart(cartItemId)
    }
}