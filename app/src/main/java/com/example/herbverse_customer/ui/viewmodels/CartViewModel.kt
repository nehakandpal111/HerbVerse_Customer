package com.example.herbverse_customer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.domain.model.CartItem
import com.example.herbverse_customer.domain.usecase.cart.AddToCartUseCase
import com.example.herbverse_customer.domain.usecase.cart.GetCartItemsUseCase
import com.example.herbverse_customer.domain.usecase.cart.RemoveFromCartUseCase
import com.example.herbverse_customer.domain.usecase.cart.UpdateCartQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for cart operations
 */
class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val cartItems = getCartItemsUseCase()
                _uiState.update {
                    it.copy(
                        cartItems = cartItems,
                        total = cartItems.sumOf { item -> item.totalPrice },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load cart items: ${e.message}"
                    )
                }
            }
        }
    }

    fun addToCart(productId: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val success = addToCartUseCase(productId, quantity)
                if (success) {
                    loadCartItems()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to add item to cart"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error adding to cart: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val success = updateCartQuantityUseCase(cartItemId, newQuantity)
                if (success) {
                    loadCartItems()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to update quantity"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error updating quantity: ${e.message}"
                    )
                }
            }
        }
    }

    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val success = removeFromCartUseCase(cartItemId)
                if (success) {
                    loadCartItems()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to remove item"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error removing item: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearCart() {
        // Implementation will be added
    }

    fun checkout() {
        // Navigate to checkout screen
    }

    /**
     * UI state for cart
     */
    data class CartUiState(
        val cartItems: List<CartItem> = emptyList(),
        val total: Double = 0.0,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}