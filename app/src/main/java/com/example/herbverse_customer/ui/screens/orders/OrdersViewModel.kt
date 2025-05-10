package com.example.herbverse_customer.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.models.Order
import com.example.herbverse_customer.models.OrderStatus
import com.example.herbverse_customer.models.TrackingEvent
import com.example.herbverse_customer.orders.OrderService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing orders and order tracking
 */
class OrdersViewModel(
    private val orderService: OrderService,
    private val userId: String // Ideally injected from user session/auth
) : ViewModel() {

    // UI state for orders list screen
    data class OrdersUiState(
        val isLoading: Boolean = true,
        val orders: List<Order> = emptyList(),
        val activeOrders: List<Order> = emptyList(),
        val pastOrders: List<Order> = emptyList(),
        val errorMessage: String? = null
    )
    
    // UI state for order tracking screen
    data class TrackingUiState(
        val isLoading: Boolean = true,
        val order: Order? = null,
        val trackingEvents: List<TrackingEvent> = emptyList(),
        val errorMessage: String? = null
    )
    
    // Orders list UI state
    private val _ordersUiState = MutableStateFlow(OrdersUiState())
    val ordersUiState: StateFlow<OrdersUiState> = _ordersUiState
    
    // Order tracking UI state
    private val _trackingUiState = MutableStateFlow(TrackingUiState())
    val trackingUiState: StateFlow<TrackingUiState> = _trackingUiState
    
    init {
        loadOrders()
    }
    
    /**
     * Load all orders for the current user
     */
    fun loadOrders() {
        viewModelScope.launch {
            _ordersUiState.update { it.copy(isLoading = true) }
            
            orderService.getOrders(userId)
                .catch { e ->
                    _ordersUiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error loading orders: ${e.message}"
                        ) 
                    }
                }
                .collect { orders ->
                    // Sort orders by date, most recent first
                    val sortedOrders = orders.sortedByDescending { it.orderDate }
                    
                    // Split orders into active and past
                    val (active, past) = sortedOrders.partition { order ->
                        order.status == OrderStatus.CONFIRMED || 
                        order.status == OrderStatus.PROCESSING || 
                        order.status == OrderStatus.PENDING ||
                        order.status == OrderStatus.SHIPPED || 
                        order.status == OrderStatus.OUT_FOR_DELIVERY
                    }
                    
                    _ordersUiState.update { 
                        it.copy(
                            isLoading = false,
                            orders = sortedOrders,
                            activeOrders = active,
                            pastOrders = past,
                            errorMessage = null
                        ) 
                    }
                }
        }
    }
    
    /**
     * Load a specific order and its tracking information
     */
    fun loadOrderTracking(orderId: String) {
        viewModelScope.launch {
            _trackingUiState.update { it.copy(isLoading = true) }
            
            // First, get the order details
            orderService.getOrderById(orderId)
                .catch { e ->
                    _trackingUiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error loading order details: ${e.message}"
                        )
                    }
                }
                .collect { order ->
                    if (order == null) {
                        _trackingUiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "Order not found"
                            )
                        }
                        return@collect
                    }
                    
                    // Then get tracking information
                    orderService.getOrderTracking(orderId)
                        .catch { e ->
                            _trackingUiState.update { 
                                it.copy(
                                    isLoading = false,
                                    order = order,
                                    errorMessage = "Error loading tracking: ${e.message}"
                                )
                            }
                        }
                        .collect { events ->
                            _trackingUiState.update { 
                                it.copy(
                                    isLoading = false,
                                    order = order,
                                    trackingEvents = events,
                                    errorMessage = null
                                )
                            }
                        }
                }
        }
    }
    
    /**
     * Cancel an order
     */
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            orderService.cancelOrder(orderId)
                .onSuccess { success ->
                    if (success) {
                        // Refresh orders to reflect cancellation
                        loadOrders()
                        
                        // If currently viewing this order, refresh tracking
                        _trackingUiState.value.order?.let { order ->
                            if (order.id == orderId) {
                                loadOrderTracking(orderId)
                            }
                        }
                    } else {
                        _ordersUiState.update { 
                            it.copy(errorMessage = "Could not cancel order") 
                        }
                    }
                }
                .onFailure { e ->
                    _ordersUiState.update { 
                        it.copy(errorMessage = "Error cancelling order: ${e.message}") 
                    }
                }
        }
    }
    
    /**
     * Calculate progress percentage for order status
     */
    fun getOrderProgressPercentage(order: Order): Float {
        return when (order.status) {
            OrderStatus.PENDING -> 0.10f
            OrderStatus.CONFIRMED -> 0.25f
            OrderStatus.PROCESSING -> 0.5f
            OrderStatus.SHIPPED -> 0.75f
            OrderStatus.OUT_FOR_DELIVERY -> 0.90f
            OrderStatus.DELIVERED -> 1.0f
            else -> 0f // CANCELLED, RETURNED
        }
    }
    
    /**
     * Get user-friendly status description
     */
    fun getOrderStatusDescription(order: Order): String {
        return when (order.status) {
            OrderStatus.PENDING -> "Order pending"
            OrderStatus.CONFIRMED -> "Order confirmed"
            OrderStatus.PROCESSING -> "Preparing your order"
            OrderStatus.SHIPPED -> "Order shipped"
            OrderStatus.OUT_FOR_DELIVERY -> "Out for delivery today"
            OrderStatus.DELIVERED -> "Delivered"
            OrderStatus.CANCELLED -> "Cancelled"
            OrderStatus.RETURNED -> "Returned"
        }
    }
}