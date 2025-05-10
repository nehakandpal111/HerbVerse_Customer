package com.example.herbverse_customer.domain.usecase.cart

import com.example.herbverse_customer.domain.repository.CartRepository

/**
 * Use case for adding items to cart
 */
class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: String, quantity: Int): Boolean {
        return cartRepository.addToCart(productId, quantity)
    }
}