package com.example.herbverse_customer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.data.model.Category
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.services.CustomerProductService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerProductViewModel(private val productService: CustomerProductService) : ViewModel() {
    // For product listings
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    // For categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    // For selected product details
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()
    
    // For search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // For filtered products
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()
    
    // For loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // For error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Selected category
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()
    
    init {
        loadAllProducts()
        loadCategories()
    }
    
    // Load all available products
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = productService.getAllProducts()
                
                if (result.isSuccess) {
                    _products.value = result.getOrNull() ?: emptyList()
                    _filteredProducts.value = _products.value
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to load products"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load all product categories
    fun loadCategories() {
        viewModelScope.launch {
            _errorMessage.value = null
            
            try {
                val result = productService.getAllCategories()
                
                if (result.isSuccess) {
                    _categories.value = result.getOrNull() ?: emptyList()
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to load categories"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            }
        }
    }
    
    // Load products by category
    fun loadProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = productService.getProductsByCategory(categoryId)
                
                if (result.isSuccess) {
                    _products.value = result.getOrNull() ?: emptyList()
                    _filteredProducts.value = _products.value
                    
                    // Update selected category
                    _selectedCategory.value = _categories.value.find { it.id == categoryId }
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to load products"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load product details
    fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = productService.getProductDetails(productId)
                
                if (result.isSuccess) {
                    _selectedProduct.value = result.getOrNull()
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to load product details"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Search products by name or description
    fun searchProducts(query: String) {
        _searchQuery.value = query
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                if (query.isBlank()) {
                    // Reset to original list if query is empty
                    _filteredProducts.value = _products.value
                } else {
                    val result = productService.searchProducts(query)
                    
                    if (result.isSuccess) {
                        _filteredProducts.value = result.getOrNull() ?: emptyList()
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to search products"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Reset selected category
    fun clearCategoryFilter() {
        _selectedCategory.value = null
        loadAllProducts()
    }
    
    // Clear selected product
    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
    
    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}