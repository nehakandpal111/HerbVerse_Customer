package com.example.herbverse_customer.data

import android.util.Log
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.data.model.Category
import com.example.herbverse_customer.data.model.CartItem
import com.example.herbverse_customer.domain.model.CartItem as DomainCartItem
import com.example.herbverse_customer.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRepository(private val useMockData: Boolean = true) : CartRepository {
    private val TAG = "FirestoreRepository"
    
    // Firebase instances
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    
    // Mock data
    private val mockProducts = listOf(
        Product(
            id = "prod1", 
            name = "Mock Chamomile", 
            shortDescription = "A calming herb good for sleep",
            fullDescription = "A calming herb good for sleep and relaxation. Has been used for centuries.",
            price = 5.99,
            stock = 100,
            categoryId = "cat1",
            imageUrl = "https://example.com/chamomile.jpg"
        ),
        Product(
            id = "prod2", 
            name = "Mock Lavender", 
            shortDescription = "Aromatic herb with calming properties",
            fullDescription = "Lavender is known for its calming scent and is used in many products.",
            price = 6.99,
            stock = 85,
            categoryId = "cat1",
            imageUrl = "https://example.com/lavender.jpg"
        ),
        Product(
            id = "prod3", 
            name = "Mock Mint", 
            shortDescription = "Refreshing herb for digestion",
            fullDescription = "Mint is commonly used to aid digestion and add flavor to food and drinks.",
            price = 4.99,
            stock = 120,
            categoryId = "cat2",
            imageUrl = "https://example.com/mint.jpg"
        ),
        Product(
            id = "prod4", 
            name = "Green Tea", 
            shortDescription = "Classic herbal tea for relaxation",
            fullDescription = "Green tea is known for its health benefits and antioxidant properties.",
            price = 7.99,
            stock = 90,
            categoryId = "cat4",
            imageUrl = "https://example.com/greentea.jpg"
        ),
        Product(
            id = "prod5", 
            name = "Chamomile Tea", 
            shortDescription = "Soothing evening tea blend",
            fullDescription = "Chamomile tea is perfect for relaxation and improving sleep quality.",
            price = 6.49,
            stock = 75,
            categoryId = "cat4",
            imageUrl = "https://example.com/chamomiletea.jpg"
        ),
        Product(
            id = "prod6", 
            name = "Lavender Oil", 
            shortDescription = "Pure essential oil for aromatherapy",
            fullDescription = "Lavender essential oil for aromatherapy and relaxation.",
            price = 12.99,
            stock = 50,
            categoryId = "cat5",
            imageUrl = "https://example.com/lavenderoil.jpg"
        ),
        Product(
            id = "prod7", 
            name = "Tea Tree Oil", 
            shortDescription = "Natural antiseptic oil",
            fullDescription = "Tea tree oil has natural antiseptic properties and is used for skin care.",
            price = 10.99,
            stock = 45,
            categoryId = "cat5",
            imageUrl = "https://example.com/teatreeoil.jpg"
        ),
        Product(
            id = "prod8", 
            name = "Eucalyptus Bark", 
            shortDescription = "Natural remedy for respiratory issues",
            fullDescription = "Eucalyptus bark is used for respiratory support and has a refreshing scent.",
            price = 8.49,
            stock = 60,
            categoryId = "cat6",
            imageUrl = "https://example.com/eucalyptusbark.jpg"
        ),
        Product(
            id = "prod9", 
            name = "Willow Bark", 
            shortDescription = "Natural pain reliever",
            fullDescription = "Willow bark has been used as a natural pain reliever for centuries.",
            price = 9.99,
            stock = 40,
            categoryId = "cat6",
            imageUrl = "https://example.com/willowbark.jpg"
        )
    )
    
    private val mockCategories = listOf(
        Category(id = "cat1", name = "Calming Herbs"),
        Category(id = "cat2", name = "Digestive Herbs"),
        Category(id = "cat3", name = "Medicinal Herbs"),
        Category(id = "cat4", name = "Herbal Teas"),
        Category(id = "cat5", name = "Essential Oils"),
        Category(id = "cat6", name = "Barks & Roots")
    )
    
    private val mockCartItems = mutableListOf<CartItem>()
    
    // Cart data flow
    private val _cartItemsFlow = MutableStateFlow<List<DomainCartItem>>(emptyList())
    private val _cartCountFlow = MutableStateFlow(0)
    
    // Get all products
    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        if (useMockData) {
            Log.d(TAG, "Getting mock products data")
            return@withContext mockProducts
        } else {
            try {
                val snapshot = firestore.collection("products").get().await()
                return@withContext snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting products", e)
                return@withContext emptyList()
            }
        }
    }
    
    // Get products as Flow
    fun getProductsFlow(): Flow<List<Product>> = flow {
        if (useMockData) {
            Log.d(TAG, "Getting mock products flow")
            emit(mockProducts)
        } else {
            try {
                val snapshot = firestore.collection("products").get().await()
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                emit(products)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting products flow", e)
                emit(emptyList())
            }
        }
    }
    
    // Get all categories
    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        if (useMockData) {
            Log.d(TAG, "Getting mock categories data")
            return@withContext mockCategories
        } else {
            try {
                val snapshot = firestore.collection("categories").get().await()
                return@withContext snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting categories", e)
                return@withContext emptyList()
            }
        }
    }
    
    // Get products by category
    suspend fun getProductsByCategory(categoryId: String): List<Product> = withContext(Dispatchers.IO) {
        if (useMockData) {
            Log.d(TAG, "Getting mock products by category")
            return@withContext mockProducts.filter { it.categoryId == categoryId }
        } else {
            try {
                val snapshot = firestore.collection("products")
                    .whereEqualTo("categoryId", categoryId)
                    .get()
                    .await()
                
                return@withContext snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting products by category", e)
                return@withContext emptyList()
            }
        }
    }
    
    // Get current user ID
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "anonymous-user"
    }
    
    // Add item to user cart
    suspend fun addToCart(userId: String, cartItem: CartItem): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Adding to mock cart")
        mockCartItems.add(cartItem)
        return@withContext true
    }
    
    // Get user cart
    suspend fun getUserCart(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Getting mock cart items")
        return@withContext mockCartItems
    }
    
    // Cart Repository Implementation
    override suspend fun getCartItems(): List<DomainCartItem> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        
        if (useMockData) {
            val product = mockProducts.random()
            val items = listOf(
                DomainCartItem(
                    id = "cart1",
                    productId = product.id,
                    quantity = 2,
                    price = product.price,
                    name = product.name,
                    imageUrl = product.imageUrl,
                    totalPrice = product.price * 2
                )
            )
            _cartItemsFlow.value = items
            _cartCountFlow.value = items.sumOf { it.quantity }
            return@withContext items
        } else {
            try {
                val snapshot = firestore.collection("carts")
                    .document(userId)
                    .collection("items")
                    .get()
                    .await()
                
                val cartItems = snapshot.documents.map { doc ->
                    val data = doc.data
                    DomainCartItem(
                        id = doc.id,
                        productId = data?.get("productId") as? String ?: "",
                        quantity = (data?.get("quantity") as? Long)?.toInt() ?: 0,
                        price = (data?.get("price") as? Double) ?: 0.0,
                        name = data?.get("name") as? String ?: "",
                        imageUrl = data?.get("imageUrl") as? String ?: "",
                        totalPrice = (data?.get("price") as? Double)?.times(
                            (data?.get("quantity") as? Long)?.toInt() ?: 0
                        ) ?: 0.0
                    )
                }
                
                _cartItemsFlow.value = cartItems
                _cartCountFlow.value = cartItems.sumOf { it.quantity }
                
                return@withContext cartItems
            } catch (e: Exception) {
                Log.e(TAG, "Error getting cart items", e)
                return@withContext emptyList()
            }
        }
    }
    
    override fun getCartItemsFlow(): Flow<List<DomainCartItem>> {
        return _cartItemsFlow.asStateFlow()
    }
    
    override suspend fun addToCart(productId: String, quantity: Int): Boolean = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        
        if (useMockData) {
            Log.d(TAG, "Adding to mock cart: $productId, qty: $quantity")
            val product = mockProducts.find { it.id == productId } ?: return@withContext false
            
            // Check if the item is already in cart
            val existingItem = _cartItemsFlow.value.find { it.productId == productId }
            
            val cartItems = if (existingItem != null) {
                _cartItemsFlow.value.map {
                    if (it.productId == productId) {
                        it.copy(quantity = it.quantity + quantity, totalPrice = it.price * (it.quantity + quantity))
                    } else {
                        it
                    }
                }
            } else {
                _cartItemsFlow.value + DomainCartItem(
                    id = "cart${System.currentTimeMillis()}",
                    productId = product.id,
                    quantity = quantity,
                    price = product.price,
                    name = product.name,
                    imageUrl = product.imageUrl,
                    totalPrice = product.price * quantity
                )
            }
            
            _cartItemsFlow.value = cartItems
            _cartCountFlow.value = cartItems.sumOf { it.quantity }
            
            return@withContext true
        } else {
            try {
                // Get product details
                val productDoc = firestore.collection("products").document(productId).get().await()
                val product = productDoc.toObject(Product::class.java)?.copy(id = productDoc.id)
                    ?: return@withContext false
                
                // Check if item already exists in cart
                val cartRef = firestore.collection("carts").document(userId).collection("items")
                val existingItems = cartRef.whereEqualTo("productId", productId).get().await()
                
                if (existingItems.isEmpty) {
                    // Add new item
                    val cartItem = hashMapOf(
                        "productId" to productId,
                        "quantity" to quantity,
                        "price" to product.price,
                        "name" to product.name,
                        "imageUrl" to product.imageUrl,
                        "addedAt" to com.google.firebase.Timestamp.now()
                    )
                    
                    cartRef.add(cartItem).await()
                } else {
                    // Update existing item
                    val doc = existingItems.documents.first()
                    val currentQty = doc.getLong("quantity")?.toInt() ?: 0
                    cartRef.document(doc.id).update("quantity", currentQty + quantity).await()
                }
                
                // Update local flow
                getCartItems()
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to cart", e)
                return@withContext false
            }
        }
    }
    
    override suspend fun updateCartItem(cartItemId: String, quantity: Int): Boolean = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        
        if (useMockData) {
            val cartItems = _cartItemsFlow.value.map {
                if (it.id == cartItemId) {
                    it.copy(quantity = quantity, totalPrice = it.price * quantity)
                } else {
                    it
                }
            }.filter { it.quantity > 0 } // Remove items with 0 quantity
            
            _cartItemsFlow.value = cartItems
            _cartCountFlow.value = cartItems.sumOf { it.quantity }
            return@withContext true
        } else {
            try {
                val cartRef = firestore.collection("carts").document(userId).collection("items")
                
                if (quantity <= 0) {
                    // Remove item if quantity is 0 or negative
                    cartRef.document(cartItemId).delete().await()
                } else {
                    // Update quantity
                    cartRef.document(cartItemId).update("quantity", quantity).await()
                }
                
                // Update local flow
                getCartItems()
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error updating cart item", e)
                return@withContext false
            }
        }
    }
    
    override suspend fun removeFromCart(cartItemId: String): Boolean = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        
        if (useMockData) {
            val cartItems = _cartItemsFlow.value.filter { it.id != cartItemId }
            _cartItemsFlow.value = cartItems
            _cartCountFlow.value = cartItems.sumOf { it.quantity }
            return@withContext true
        } else {
            try {
                firestore.collection("carts")
                    .document(userId)
                    .collection("items")
                    .document(cartItemId)
                    .delete()
                    .await()
                
                // Update local flow
                getCartItems()
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error removing from cart", e)
                return@withContext false
            }
        }
    }
    
    override suspend fun clearCart(): Boolean = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()
        
        if (useMockData) {
            _cartItemsFlow.value = emptyList()
            _cartCountFlow.value = 0
            return@withContext true
        } else {
            try {
                val cartRef = firestore.collection("carts").document(userId).collection("items")
                val snapshot = cartRef.get().await()
                
                // Delete all items
                for (doc in snapshot.documents) {
                    cartRef.document(doc.id).delete().await()
                }
                
                // Update local flow
                _cartItemsFlow.value = emptyList()
                _cartCountFlow.value = 0
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing cart", e)
                return@withContext false
            }
        }
    }
    
    override suspend fun getCartTotal(): Double {
        return _cartItemsFlow.value.sumOf { it.totalPrice }
    }
    
    override suspend fun getCartCount(): Int {
        return _cartItemsFlow.value.sumOf { it.quantity }
    }
    
    override fun getCartCountFlow(): Flow<Int> {
        return _cartCountFlow.asStateFlow()
    }
}