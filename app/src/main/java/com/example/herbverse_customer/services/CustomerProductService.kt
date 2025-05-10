package com.example.herbverse_customer.services

import android.util.Log
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CustomerProductService {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")
    private val categoriesCollection = db.collection("categories")
    private val TAG = "CustomerProductService"
    
    // Customers can only view products, not edit them
    suspend fun getAllProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = productsCollection
                .whereGreaterThan("stock", 0) // Only show products in stock
                .orderBy("stock", Query.Direction.DESCENDING)
                .get()
                .await()
                
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all products", e)
            Result.failure(e)
        }
    }
    
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = productsCollection
                .whereEqualTo("categoryId", categoryId)
                .whereGreaterThan("stock", 0) // Only show products in stock
                .get()
                .await()
                
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products by category", e)
            Result.failure(e)
        }
    }
    
    suspend fun searchProducts(query: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        return@withContext try {
            // In a real app, you might use Algolia or another search provider
            // This is a simple implementation that searches locally after fetching all products
            val snapshot = productsCollection
                .whereGreaterThan("stock", 0)
                .get()
                .await()
                
            val allProducts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            
            val lowercaseQuery = query.lowercase()
            val filteredProducts = allProducts.filter { product ->
                product.name.lowercase().contains(lowercaseQuery) || 
                (product.shortDescription?.lowercase()?.contains(lowercaseQuery) ?: false) ||
                (product.fullDescription?.lowercase()?.contains(lowercaseQuery) ?: false)
            }
            
            Result.success(filteredProducts)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products", e)
            Result.failure(e)
        }
    }
    
    suspend fun getProductDetails(productId: String): Result<Product> = withContext(Dispatchers.IO) {
        return@withContext try {
            val documentSnapshot = productsCollection.document(productId).get().await()
            
            if (documentSnapshot.exists()) {
                val product = documentSnapshot.toObject(Product::class.java)
                    ?.copy(id = documentSnapshot.id)
                
                if (product != null) {
                    Result.success(product)
                } else {
                    Result.failure(Exception("Failed to parse product data"))
                }
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product details", e)
            Result.failure(e)
        }
    }
    
    suspend fun getAllCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = categoriesCollection.get().await()
            
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Category::class.java)?.copy(id = doc.id)
            }
            
            Result.success(categories)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all categories", e)
            Result.failure(e)
        }
    }
}