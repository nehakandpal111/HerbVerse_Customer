package com.example.herbverse_customer

import android.app.Application
import android.content.Context
import android.util.Log
import java.lang.ref.WeakReference
import com.example.herbverse_customer.data.FirestoreRepository
import com.example.herbverse_customer.data.ProductViewModel
import com.example.herbverse_customer.auth.AuthRepository
import com.example.herbverse_customer.data.repository.CategoryRepositoryImpl
import com.example.herbverse_customer.data.repository.ProductRepositoryImpl
import com.example.herbverse_customer.domain.repository.CategoryRepository
import com.example.herbverse_customer.domain.repository.ProductRepository
import com.example.herbverse_customer.domain.usecase.GetProductsByCategoryUseCase
import com.example.herbverse_customer.domain.usecase.GetProductsUseCase
import com.example.herbverse_customer.domain.usecase.cart.AddToCartUseCase
import com.example.herbverse_customer.domain.usecase.cart.GetCartItemsUseCase
import com.example.herbverse_customer.domain.usecase.cart.RemoveFromCartUseCase
import com.example.herbverse_customer.domain.usecase.cart.UpdateCartQuantityUseCase
import com.example.herbverse_customer.ui.viewmodels.ProductBrowseViewModel
import com.example.herbverse_customer.ui.viewmodels.CartViewModel
import androidx.navigation.NavController

class HerbverseApplication : Application() {
    
    // Repositories and ViewModels - use lazy initialization to defer potential crashes
    val firestoreRepository by lazy { 
        FirestoreRepository(true)
    }
    
    val productViewModel by lazy {
        ProductViewModel(firestoreRepository)
    }
    
    val authRepository by lazy {
        AuthRepository(applicationContext)
    }
    
    // Domain repositories
    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(firestoreRepository)
    }
    
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(firestoreRepository)
    }
    
    // Use cases
    val getProductsUseCase by lazy {
        GetProductsUseCase(productRepository)
    }
    
    val getProductsByCategoryUseCase by lazy {
        GetProductsByCategoryUseCase(productRepository)
    }
    
    // Cart use cases
    val getCartItemsUseCase by lazy {
        GetCartItemsUseCase(firestoreRepository)
    }
    
    val addToCartUseCase by lazy {
        AddToCartUseCase(firestoreRepository)
    }
    
    // ViewModels
    val productBrowseViewModel by lazy {
        ProductBrowseViewModel(getProductsUseCase, getProductsByCategoryUseCase)
    }
    
    val cartViewModel by lazy {
        CartViewModel(
            getCartItemsUseCase = GetCartItemsUseCase(firestoreRepository),
            addToCartUseCase = AddToCartUseCase(firestoreRepository),
            updateCartQuantityUseCase = UpdateCartQuantityUseCase(firestoreRepository),
            removeFromCartUseCase = RemoveFromCartUseCase(firestoreRepository)
        )
    }
    
    // Navigation controller for screen navigation from anywhere
    var navController: NavController? = null

    companion object {
        private const val TAG = "HerbverseApp"
        private var instance: WeakReference<HerbverseApplication>? = null
        
        fun getInstance(): HerbverseApplication? {
            return instance?.get()
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate started")
        
        try {
            instance = WeakReference(this)
            Log.d(TAG, "Application instance initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in application onCreate", e)
        }
        
        Log.d(TAG, "Application onCreate completed")
    }
}