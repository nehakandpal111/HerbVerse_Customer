package com.example.herbverse_customer.models

/**
 * Model class representing a shipping address
 */
data class Address(
    val id: String = "",
    val name: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val phone: String = "",
    val isDefault: Boolean = false
)