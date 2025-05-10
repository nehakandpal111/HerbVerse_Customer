package com.example.herbverse_customer.domain.model

/**
 * Domain model for a product
 */
data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val price: Double,
    val stock: Int,
    val categoryId: String,
    val imageUrl: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val vendorId: String = "",
    val isFavorite: Boolean = false
)