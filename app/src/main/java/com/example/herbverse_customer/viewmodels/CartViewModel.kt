package com.example.herbverse_customer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.domain.model.CartItem
import com.example.herbverse_customer.domain.repository.CartRepository
import com.example.herbverse_customer.services.CustomerProductService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productService: CustomerProductService
) : ViewModel() {
    // Cart items
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    // Cart total
    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()
    
    // Cart item count
    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadCartItems()
    }
    
    // Load cart items from repository
    private fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val items = cartRepository.getCartItems()
                _cartItems.value = items
                _cartCount.value = items.sumOf { it.quantity }
                _cartTotal.value = calculateCartTotal(items)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Add item to cart
    fun addToCart(product: Product, quantity: Int = 1) {
        if (quantity <= 0) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val success = cartRepository.addToCart(product.id, quantity)
                
                if (success) {
                    // Reload cart items to reflect the changes
                    loadCartItems()
                } else {
                    _errorMessage.value = "Failed to add item to cart"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding to cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Update cart item quantity
    fun updateItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity < 0) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                if (newQuantity == 0) {
                    // Remove item if quantity is 0
                    cartRepository.removeFromCart(cartItemId)
                } else {
                    cartRepository.updateCartItem(cartItemId, newQuantity)
                }
                
                // Reload cart to reflect changes
                loadCartItems()
            } catch (e: Exception) {
                _errorMessage.value = "Error updating cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Increment item quantity
    fun incrementItemQuantity(cartItemId: String) {
        val item = _cartItems.value.find { it.id == cartItemId } ?: return
        updateItemQuantity(cartItemId, item.quantity + 1)
    }
    
    // Decrement item quantity
    fun decrementItemQuantity(cartItemId: String) {
        val item = _cartItems.value.find { it.id == cartItemId } ?: return
        val newQuantity = max(0, item.quantity - 1)
        updateItemQuantity(cartItemId, newQuantity)
    }
    
    // Remove item from cart
    fun removeCartItem(cartItemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val success = cartRepository.removeFromCart(cartItemId)
                
                if (success) {
                    // Reload cart items to reflect the changes
                    loadCartItems()
                } else {
                    _errorMessage.value = "Failed to remove item from cart"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error removing from cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Clear entire cart
    fun clearCart() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val success = cartRepository.clearCart()
                
                if (success) {
                    _cartItems.value = emptyList()
                    _cartCount.value = 0
                    _cartTotal.value = 0.0
                } else {
                    _errorMessage.value = "Failed to clear cart"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error clearing cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Helper to calculate cart total
    private fun calculateCartTotal(items: List<CartItem>): Double {
        return items.sumOf { it.totalPrice }
    }
    
    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Refresh cart data
    fun refreshCart() {
        loadCartItems()
    }
}