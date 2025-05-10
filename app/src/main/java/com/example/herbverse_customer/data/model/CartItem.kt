package com.example.herbverse_customer.data.model

data class CartItem(
    val id: String = "",
    val productId: String,
    val quantity: Int,
    val price: Double
)