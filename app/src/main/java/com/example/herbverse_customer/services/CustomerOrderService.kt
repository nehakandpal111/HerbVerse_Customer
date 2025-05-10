package com.example.herbverse_customer.services

import android.util.Log
import com.example.herbverse_customer.domain.model.CartItem
import com.example.herbverse_customer.models.Address
import com.example.herbverse_customer.models.Order
import com.example.herbverse_customer.models.OrderItem
import com.example.herbverse_customer.models.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class CustomerOrderService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")
    private val productsCollection = db.collection("products")
    private val TAG = "CustomerOrderService"
    
    // Only show customer's own orders
    suspend fun getCustomerOrders(): Result<List<Order>> = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: return@withContext Result.failure(Exception("No authenticated user"))
        
        return@withContext try {
            val snapshot = ordersCollection
                .whereEqualTo("customerId", currentUser.uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                
            val orders = snapshot.documents.mapNotNull { doc ->
                val order = doc.toObject(Order::class.java)?.copy(id = doc.id)
                order
            }
            
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting customer orders", e)
            Result.failure(e)
        }
    }
    
    // Get a specific order with details
    suspend fun getOrderDetails(orderId: String): Result<Order> = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: return@withContext Result.failure(Exception("No authenticated user"))
        
        return@withContext try {
            val documentSnapshot = ordersCollection.document(orderId).get().await()
            
            if (!documentSnapshot.exists()) {
                return@withContext Result.failure(Exception("Order not found"))
            }
            
            val order = documentSnapshot.toObject(Order::class.java)?.copy(id = documentSnapshot.id)
            
            if (order == null) {
                Result.failure(Exception("Failed to parse order data"))
            } else if (order.customerId != currentUser.uid) {
                Result.failure(Exception("Unauthorized access to order"))
            } else {
                Result.success(order)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting order details", e)
            Result.failure(e)
        }
    }
    
    // Place a new order
    suspend fun placeOrder(
        items: List<CartItem>,
        shippingAddress: Address,
        paymentMethod: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: return@withContext Result.failure(Exception("No authenticated user"))
        
        if (items.isEmpty()) {
            return@withContext Result.failure(Exception("Cannot place an empty order"))
        }
        
        return@withContext try {
            // Group items by vendorId for processing
            val itemsByVendor = items.groupBy { it.vendorId ?: "unknown" }
            
            var mainOrderId = ""
            
            // Use a transaction to ensure all operations succeed or fail together
            db.runTransaction { transaction ->
                var totalAmount = 0.0
                val orderItems = mutableListOf<OrderItem>()
                
                // Verify each product and update stock
                for (item in items) {
                    val productRef = productsCollection.document(item.productId)
                    val productSnapshot = transaction.get(productRef)
                    
                    if (!productSnapshot.exists()) {
                        throw Exception("Product ${item.productId} not found")
                    }
                    
                    val currentStock = productSnapshot.getLong("stock") ?: 0
                    
                    if (currentStock < item.quantity) {
                        throw Exception("Not enough stock for ${item.name}. Available: $currentStock")
                    }
                    
                    // Update product stock
                    transaction.update(productRef, "stock", currentStock - item.quantity)
                    
                    val price = productSnapshot.getDouble("price") ?: 0.0
                    val itemTotal = price * item.quantity
                    totalAmount += itemTotal
                    
                    // Create order item
                    orderItems.add(
                        OrderItem(
                            productId = item.productId,
                            name = item.name,
                            price = price,
                            quantity = item.quantity,
                            total = itemTotal,
                            imageUrl = item.imageUrl ?: "",
                            vendorId = productSnapshot.getString("vendorId") ?: ""
                        )
                    )
                }
                
                // Create main customer order
                val mainOrder = hashMapOf(
                    "customerId" to currentUser.uid,
                    "items" to orderItems,
                    "status" to OrderStatus.PENDING,
                    "total" to totalAmount,
                    "shippingAddress" to hashMapOf(
                        "name" to shippingAddress.name,
                        "street" to shippingAddress.street,
                        "city" to shippingAddress.city,
                        "state" to shippingAddress.state,
                        "zipCode" to shippingAddress.zipCode,
                        "country" to shippingAddress.country,
                        "phone" to shippingAddress.phone
                    ),
                    "paymentMethod" to paymentMethod,
                    "paymentStatus" to "pending",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                
                // Create the main order document
                val mainOrderRef = ordersCollection.document()
                transaction.set(mainOrderRef, mainOrder)
                mainOrderId = mainOrderRef.id
                
                // Create vendor-specific orders
                for ((vendorId, vendorItems) in itemsByVendor) {
                    if (vendorId == "unknown") continue
                    
                    val vendorOrderItems = orderItems.filter { it.vendorId == vendorId }
                    val vendorTotal = vendorOrderItems.sumOf { it.total }
                    
                    val vendorOrder = hashMapOf(
                        "mainOrderId" to mainOrderRef.id,
                        "customerId" to currentUser.uid,
                        "vendorId" to vendorId,
                        "items" to vendorOrderItems,
                        "status" to OrderStatus.PENDING,
                        "total" to vendorTotal,
                        "shippingAddress" to hashMapOf(
                            "name" to shippingAddress.name,
                            "street" to shippingAddress.street,
                            "city" to shippingAddress.city,
                            "state" to shippingAddress.state,
                            "zipCode" to shippingAddress.zipCode,
                            "country" to shippingAddress.country,
                            "phone" to shippingAddress.phone
                        ),
                        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )
                    
                    val vendorOrderRef = db.collection("vendor-orders").document()
                    transaction.set(vendorOrderRef, vendorOrder)
                }
            }.await()
            
            Result.success(mainOrderId)
        } catch (e: Exception) {
            Log.e(TAG, "Error placing order", e)
            Result.failure(e)
        }
    }
    
    // Cancel an order (only if it's still pending)
    suspend fun cancelOrder(orderId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser ?: return@withContext Result.failure(Exception("No authenticated user"))
        
        return@withContext try {
            val orderRef = ordersCollection.document(orderId)
            val orderSnapshot = orderRef.get().await()
            
            if (!orderSnapshot.exists()) {
                return@withContext Result.failure(Exception("Order not found"))
            }
            
            val customerId = orderSnapshot.getString("customerId")
            
            if (customerId != currentUser.uid) {
                return@withContext Result.failure(Exception("Unauthorized access to order"))
            }
            
            val status = orderSnapshot.getString("status") ?: ""
            
            if (status != OrderStatus.PENDING.toString()) {
                return@withContext Result.failure(Exception("Can only cancel orders with PENDING status"))
            }
            
            // Use a transaction to restore stock and update orders
            db.runTransaction { transaction ->
                // Update main order status
                transaction.update(orderRef, "status", OrderStatus.CANCELLED)
                
                // Get all items to restore stock
                val items = orderSnapshot.get("items") as? List<Map<String, Any>> ?: listOf()
                
                // Restore stock for each product
                for (item in items) {
                    val productId = item["productId"] as? String ?: continue
                    val quantity = (item["quantity"] as? Long)?.toInt() ?: continue
                    
                    val productRef = productsCollection.document(productId)
                    val productSnapshot = transaction.get(productRef)
                    
                    if (productSnapshot.exists()) {
                        val currentStock = productSnapshot.getLong("stock") ?: 0
                        transaction.update(productRef, "stock", currentStock + quantity)
                    }
                }
            }.await()
            
            // Now update vendor orders separately after the main transaction
            val vendorOrdersQuery = db.collection("vendor-orders")
                .whereEqualTo("mainOrderId", orderId)
                .get()
                .await()
            
            // Create a batch for updating vendor orders
            val batch = db.batch()
            for (vendorOrderDoc in vendorOrdersQuery.documents) {
                batch.update(vendorOrderDoc.reference, "status", OrderStatus.CANCELLED)
            }
            if (vendorOrdersQuery.documents.isNotEmpty()) {
                batch.commit().await()
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling order", e)
            Result.failure(e)
        }
    }
}