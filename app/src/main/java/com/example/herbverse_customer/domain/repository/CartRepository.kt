package com.example.herbverse_customer.domain.repository

import com.example.herbverse_customer.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cart operations
 */
interface CartRepository {
    suspend fun getCartItems(): List<CartItem>
    fun getCartItemsFlow(): Flow<List<CartItem>>
    suspend fun addToCart(productId: String, quantity: Int): Boolean
    suspend fun updateCartItem(cartItemId: String, quantity: Int): Boolean
    suspend fun removeFromCart(cartItemId: String): Boolean
    suspend fun clearCart(): Boolean
    suspend fun getCartTotal(): Double
    suspend fun getCartCount(): Int
    fun getCartCountFlow(): Flow<Int>
}