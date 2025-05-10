package com.example.herbverse_customer.domain.model

/**
 * Domain model for a product category
 */
data class Category(
    val id: String,
    val name: String,
    val description: String = "",
    val imageUrl: String = "",
    val productCount: Int = 0
)