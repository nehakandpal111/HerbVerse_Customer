package com.example.herbverse_customer.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.data.model.Category
import com.example.herbverse_customer.data.model.CartItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class ProductViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    
    // LiveData for products
    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products
    
    // LiveData for categories
    private val _categories = MutableLiveData<List<Category>>(emptyList())
    val categories: LiveData<List<Category>> = _categories
    
    // LiveData for cart items
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    // Initialize by loading data
    init {
        loadProducts()
        loadCategories()
    }
    
    // Load all products
    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val productsList = firestoreRepository.getProducts()
                _products.value = productsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load products by category
    fun loadProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val productsList = firestoreRepository.getProductsByCategory(categoryId)
                _products.value = productsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products by category: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load all categories
    fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val categoriesList = firestoreRepository.getCategories()
                _categories.value = categoriesList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Add item to cart
    fun addToCart(userId: String, productId: String, quantity: Int, price: Double) {
        viewModelScope.launch {
            try {
                val cartItem = CartItem(
                    productId = productId,
                    quantity = quantity,
                    price = price
                )
                
                val success = firestoreRepository.addToCart(userId, cartItem)
                
                if (success) {
                    loadCart(userId)
                } else {
                    _errorMessage.value = "Failed to add item to cart"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding to cart: ${e.message}"
            }
        }
    }
    
    // Load user cart
    fun loadCart(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val userCartItems = firestoreRepository.getUserCart(userId)
                _cartItems.value = userCartItems
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cart: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}