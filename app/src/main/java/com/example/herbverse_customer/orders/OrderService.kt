package com.example.herbverse_customer.orders

import android.util.Log
import com.example.herbverse_customer.models.Order
import com.example.herbverse_customer.models.OrderStatus
import com.example.herbverse_customer.models.OrderItem
import com.example.herbverse_customer.models.ShippingInfo
import com.example.herbverse_customer.models.TrackingEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

/**
 * Service class for managing orders.
 * In a real application, this would connect to a backend API.
 */
class OrderService {

    private val TAG = "OrderService"
    
    // In-memory cache of orders
    private val orders = mutableListOf<Order>()
    
    init {
        // Generate some mock orders for testing
        generateMockOrders()
    }
    
    /**
     * Get all orders for a user
     */
    fun getOrders(userId: String): Flow<List<Order>> = flow {
        // Simulate network delay
        delay(800)
        
        // In a real app, filter by user ID
        emit(orders)
    }
    
    /**
     * Get a specific order by ID
     */
    fun getOrderById(orderId: String): Flow<Order?> = flow {
        // Simulate network delay
        delay(500)
        
        val order = orders.find { it.id == orderId }
        emit(order)
    }
    
    /**
     * Get tracking information for an order
     */
    fun getOrderTracking(orderId: String): Flow<List<TrackingEvent>> = flow {
        // Simulate network delay
        delay(700)
        
        val order = orders.find { it.id == orderId }
        if (order != null) {
            emit(generateTrackingEvents(order))
        } else {
            emit(emptyList())
        }
    }
    
    /**
     * Place a new order
     */
    suspend fun placeOrder(
        userId: String,
        items: List<OrderItem>,
        shippingInfo: ShippingInfo,
        paymentMethod: String
    ): Result<Order> {
        return try {
            // Simulate network operation
            delay(1500)
            
            val subtotal = items.sumOf { it.price * it.quantity }
            val shippingCost = 5.99
            val tax = subtotal * 0.08 // 8% tax
            val total = subtotal + shippingCost + tax
            
            val newOrder = Order(
                id = UUID.randomUUID().toString(),
                userId = userId,
                items = items,
                shippingInfo = shippingInfo,
                orderDate = Date(),
                status = OrderStatus.CONFIRMED,
                subtotal = subtotal,
                shippingCost = shippingCost,
                tax = tax,
                total = total,
                paymentMethod = paymentMethod,
                estimatedDelivery = calculateEstimatedDelivery()
            )
            
            orders.add(newOrder)
            Log.d(TAG, "New order placed: ${newOrder.id}")
            
            Result.success(newOrder)
        } catch (e: Exception) {
            Log.e(TAG, "Error placing order", e)
            Result.failure(e)
        }
    }
    
    /**
     * Cancel an order
     */
    suspend fun cancelOrder(orderId: String): Result<Boolean> {
        return try {
            // Simulate network delay
            delay(1000)
            
            val orderIndex = orders.indexOfFirst { it.id == orderId }
            if (orderIndex != -1) {
                val order = orders[orderIndex]
                if (order.status == OrderStatus.CONFIRMED || order.status == OrderStatus.PROCESSING) {
                    val updatedOrder = order.copy(status = OrderStatus.CANCELLED)
                    orders[orderIndex] = updatedOrder
                    
                    Log.d(TAG, "Order cancelled: $orderId")
                    Result.success(true)
                } else {
                    Log.d(TAG, "Cannot cancel order in status: ${order.status}")
                    Result.failure(Exception("Cannot cancel order in status: ${order.status}"))
                }
            } else {
                Log.d(TAG, "Order not found: $orderId")
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling order", e)
            Result.failure(e)
        }
    }
    
    /**
     * Generate tracking events for an order based on its status
     */
    private fun generateTrackingEvents(order: Order): List<TrackingEvent> {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val orderDate = order.orderDate ?: Date()
        val events = mutableListOf<TrackingEvent>()
        
        // Order placed
        events.add(
            TrackingEvent(
                title = "Order Received",
                description = "Your order has been received and is being processed",
                time = formatter.format(orderDate),
                date = dateFormatter.format(orderDate),
                isCompleted = true,
                isActive = false
            )
        )
        
        // Payment confirmed - 1 hour later
        val paymentDate = Date(orderDate.time + (1 * 60 * 60 * 1000))
        events.add(
            TrackingEvent(
                title = "Payment Confirmed",
                description = "Payment has been successfully processed",
                time = formatter.format(paymentDate),
                date = dateFormatter.format(paymentDate),
                isCompleted = true,
                isActive = false
            )
        )
        
        if (order.status == OrderStatus.CANCELLED) {
            // Order cancelled
            val cancelDate = Date(paymentDate.time + (2 * 60 * 60 * 1000))
            events.add(
                TrackingEvent(
                    title = "Order Cancelled",
                    description = "This order has been cancelled",
                    time = formatter.format(cancelDate),
                    date = dateFormatter.format(cancelDate),
                    isCompleted = true,
                    isActive = true
                )
            )
            return events
        }
        
        // Order packed - 1 day later
        val packedDate = Date(orderDate.time + (24 * 60 * 60 * 1000))
        val isPacked = order.status != OrderStatus.CONFIRMED
        events.add(
            TrackingEvent(
                title = "Order Packed",
                description = "Your items have been carefully packed",
                time = formatter.format(packedDate),
                date = dateFormatter.format(packedDate),
                isCompleted = isPacked,
                isActive = order.status == OrderStatus.PROCESSING
            )
        )
        
        // Order shipped - 2 days later
        val shippedDate = Date(orderDate.time + (2 * 24 * 60 * 60 * 1000))
        val isShipped = order.status == OrderStatus.SHIPPED || order.status == OrderStatus.OUT_FOR_DELIVERY || order.status == OrderStatus.DELIVERED
        events.add(
            TrackingEvent(
                title = "Order Shipped",
                description = "Your package is on its way",
                time = formatter.format(shippedDate),
                date = dateFormatter.format(shippedDate),
                isCompleted = isShipped,
                isActive = order.status == OrderStatus.SHIPPED
            )
        )
        
        order.estimatedDelivery?.let { estimatedDelivery ->
            // Out for delivery - estimated delivery date - 1 day
            val outForDeliveryDate = Date(estimatedDelivery.time - (24 * 60 * 60 * 1000))
            val isOutForDelivery = order.status == OrderStatus.OUT_FOR_DELIVERY || order.status == OrderStatus.DELIVERED
            events.add(
                TrackingEvent(
                    title = "Out for Delivery",
                    description = "Your package is out for delivery today",
                    time = formatter.format(outForDeliveryDate),
                    date = dateFormatter.format(outForDeliveryDate),
                    isCompleted = isOutForDelivery,
                    isActive = order.status == OrderStatus.OUT_FOR_DELIVERY
                )
            )

            // Delivered - estimated delivery date
            val isDelivered = order.status == OrderStatus.DELIVERED
            events.add(
                TrackingEvent(
                    title = "Delivered",
                    description = "Your package has been delivered",
                    time = formatter.format(estimatedDelivery),
                    date = dateFormatter.format(estimatedDelivery),
                    isCompleted = isDelivered,
                    isActive = order.status == OrderStatus.DELIVERED
                )
            )
        }
        
        return events
    }
    
    /**
     * Calculate estimated delivery date (5-7 days from now)
     */
    private fun calculateEstimatedDelivery(): Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 5 + Random.nextInt(3)) // 5-7 days
        return calendar.time
    }
    
    /**
     * Generate mock orders for testing
     */
    private fun generateMockOrders() {
        // Create a few sample orders
        val mockItems1 = listOf(
            OrderItem(
                productId = "1",
                name = "Lavender",
                price = 9.99,
                quantity = 2,
                imageUrl = ""
            ),
            OrderItem(
                productId = "2",
                name = "Basil",
                price = 4.99,
                quantity = 1,
                imageUrl = ""
            )
        )
        
        val mockItems2 = listOf(
            OrderItem(
                productId = "3",
                name = "Chamomile",
                price = 7.50,
                quantity = 1,
                imageUrl = ""
            ),
            OrderItem(
                productId = "4",
                name = "Rosemary",
                price = 6.99,
                quantity = 2,
                imageUrl = ""
            ),
            OrderItem(
                productId = "5",
                name = "Peppermint",
                price = 5.99,
                quantity = 1,
                imageUrl = ""
            )
        )
        
        val shippingInfo = ShippingInfo(
            name = "John Doe",
            addressLine1 = "123 Herb Street",
            addressLine2 = "Apt 101",
            city = "Green City",
            state = "GC",
            zipCode = "12345",
            phone = "(555) 123-4567",
            instructions = "Leave package at the front door"
        )
        
        // Order 1 (In progress)
        val order1 = Order(
            id = "HV23789",
            userId = "user1",
            items = mockItems1,
            shippingInfo = shippingInfo,
            orderDate = Date(System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)), // 3 days ago
            status = OrderStatus.SHIPPED,
            subtotal = 24.97,
            shippingCost = 5.99,
            tax = 2.50,
            total = 33.46,
            paymentMethod = "Credit Card",
            estimatedDelivery = Date(System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L)) // 2 days from now
        )
        
        // Order 2 (Delivered)
        val order2 = Order(
            id = "HV22547",
            userId = "user1",
            items = mockItems2,
            shippingInfo = shippingInfo,
            orderDate = Date(System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000L)), // 10 days ago
            status = OrderStatus.DELIVERED,
            subtotal = 27.47,
            shippingCost = 5.99,
            tax = 2.75,
            total = 36.21,
            paymentMethod = "PayPal",
            estimatedDelivery = Date(System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)) // 3 days ago
        )
        
        // Add to the list
        orders.add(order1)
        orders.add(order2)
    }
}