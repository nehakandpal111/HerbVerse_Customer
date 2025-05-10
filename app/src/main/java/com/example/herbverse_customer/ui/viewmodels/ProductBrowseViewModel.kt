package com.example.herbverse_customer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.domain.model.Category
import com.example.herbverse_customer.domain.model.Product
import com.example.herbverse_customer.domain.usecase.GetProductsByCategoryUseCase
import com.example.herbverse_customer.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for product browsing
 */
class ProductBrowseViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products = getProductsUseCase()
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load products: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategoryId = categoryId) }
            try {
                val products = getProductsByCategoryUseCase(categoryId)
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load products by category: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchProducts(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // In a real app, we would debounce this and call a search API
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        loadProducts()
    }

    /**
     * UI state for product browsing
     */
    data class BrowseUiState(
        val products: List<Product> = emptyList(),
        val categories: List<Category> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val selectedCategoryId: String? = null
    )
}