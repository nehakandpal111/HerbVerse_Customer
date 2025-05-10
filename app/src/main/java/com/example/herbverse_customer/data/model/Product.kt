package com.example.herbverse_customer.data.model

data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val price: Double,
    val stock: Int,
    val categoryId: String,
    val imageUrl: String
)