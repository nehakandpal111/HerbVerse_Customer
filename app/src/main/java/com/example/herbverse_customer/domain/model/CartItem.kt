package com.example.herbverse_customer.domain.model

/**
 * Represents an item in the shopping cart
 */
data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null,
    val totalPrice: Double,
    val vendorId: String? = null
)