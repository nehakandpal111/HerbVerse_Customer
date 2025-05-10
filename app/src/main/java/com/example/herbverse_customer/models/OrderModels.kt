package com.example.herbverse_customer.models

import java.util.Date

/**
 * Represents a customer order with all associated information
 */
data class Order(
    val id: String,
    val userId: String = "",
    val customerId: String = "", // For Firestore compatibility
    val items: List<OrderItem> = listOf(),
    val shippingInfo: ShippingInfo? = null,
    val shippingAddress: Map<String, Any>? = null, // For Firestore compatibility
    val orderDate: Date? = null,
    val createdAt: Any? = null, // For Firestore Timestamp compatibility
    val status: OrderStatus = OrderStatus.PENDING,
    val subtotal: Double = 0.0,
    val shippingCost: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: String = "",
    val estimatedDelivery: Date? = null
) {
    // Helper property to get customer ID consistently
    val effectiveCustomerId: String
        get() = customerId.ifEmpty { userId }
}

/**
 * Individual item within an order
 */
data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val total: Double = price * quantity,
    val imageUrl: String? = null,
    val vendorId: String = ""
)

/**
 * Information about the shipping destination
 */
data class ShippingInfo(
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val phone: String,
    val instructions: String? = null
)

/**
 * Possible status values for an order
 */
enum class OrderStatus {
    PENDING,       // Order has been placed but not yet confirmed
    CONFIRMED,     // Order has been confirmed but not yet processed
    PROCESSING,    // Order is being processed (items picked and packed)
    SHIPPED,       // Order has been shipped
    OUT_FOR_DELIVERY, // Order is out for delivery
    DELIVERED,     // Order has been delivered
    CANCELLED,     // Order was cancelled
    RETURNED       // Order was returned
}

/**
 * Event in the order/shipment tracking timeline
 */
data class TrackingEvent(
    val title: String,
    val description: String,
    val time: String,
    val date: String,
    val isCompleted: Boolean,
    val isActive: Boolean
)