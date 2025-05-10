package com.example.herbverse_customer.data

import android.util.Log
import com.example.herbverse_customer.HerbverseApplication

/**
 * Utility class to provide easy access to ViewModels from anywhere in the app.
 */
object ViewModelProvider {
    private const val TAG = "ViewModelProvider"
    
    /**
     * Get the ProductViewModel instance.
     */
    fun getProductViewModel(): ProductViewModel {
        val app = HerbverseApplication.getInstance()
        if (app == null) {
            Log.e(TAG, "Application instance is null")
            throw IllegalStateException("Application not initialized")
        }
        
        val viewModel = app.productViewModel
        Log.d(TAG, "Returning ProductViewModel: $viewModel")
        return viewModel
    }
    
    /**
     * Get the FirestoreRepository instance.
     */
    fun getFirestoreRepository(): FirestoreRepository {
        val app = HerbverseApplication.getInstance()
        if (app == null) {
            Log.e(TAG, "Application instance is null")
            throw IllegalStateException("Application not initialized")
        }
        
        val repository = app.firestoreRepository
        Log.d(TAG, "Returning FirestoreRepository: $repository")
        return repository
    }
}