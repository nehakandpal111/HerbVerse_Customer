package com.example.herbverse_customer.auth

import android.util.Log
import com.example.herbverse_customer.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CustomerAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val customersCollection = db.collection("customers")
    private val TAG = "CustomerAuthService"
    
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String = ""
    ): Result<User> {
        return try {
            // Create the user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Create customer profile in Firestore
                val customer = hashMapOf<String, Any>(
                    "id" to firebaseUser.uid as Any,
                    "email" to email as Any,
                    "name" to name as Any
                )
                
                // Add additional fields separately to avoid potential type mismatch issues
                customer["phone"] = phone
                customer["address"] = ""
                customer["profileImage"] = ""
                customer["createdAt"] = com.google.firebase.firestore.FieldValue.serverTimestamp()
                customer["membershipTier"] = "Bronze"
                customer["rewardPoints"] = 0
                
                // Save customer data to Firestore
                customersCollection.document(firebaseUser.uid).set(customer).await()
                
                // Return success with user data
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    membershipTier = "Bronze",
                    rewardPoints = 0
                )
                
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering customer", e)
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Authenticate with Firebase
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Get user data from Firestore
                val customerDocument = customersCollection.document(firebaseUser.uid).get().await()
                
                if (customerDocument.exists()) {
                    val data = customerDocument.data
                    
                    // Create user object from Firestore data
                    val user = User(
                        id = firebaseUser.uid,
                        name = data?.get("name") as? String ?: "",
                        email = data?.get("email") as? String ?: email,
                        phone = data?.get("phone") as? String ?: "",
                        address = data?.get("address") as? String ?: "",
                        profileImageUrl = data?.get("profileImage") as? String ?: "",
                        membershipTier = data?.get("membershipTier") as? String ?: "Bronze",
                        rewardPoints = (data?.get("rewardPoints") as? Long)?.toInt() ?: 0
                    )
                    
                    Result.success(user)
                } else {
                    // Create a new customer document if it doesn't exist
                    val customer = hashMapOf<String, Any>(
                        "id" to firebaseUser.uid as Any,
                        "email" to email as Any,
                        "name" to (firebaseUser.displayName ?: "") as Any
                    )
                    
                    // Add additional fields separately to avoid type mismatch
                    customer["phone"] = firebaseUser.phoneNumber ?: ""
                    customer["createdAt"] = com.google.firebase.firestore.FieldValue.serverTimestamp()
                    customer["membershipTier"] = "Bronze"
                    customer["rewardPoints"] = 0
                    
                    customersCollection.document(firebaseUser.uid).set(customer).await()
                    
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = email,
                        phone = firebaseUser.phoneNumber ?: "",
                        membershipTier = "Bronze",
                        rewardPoints = 0
                    )
                    
                    Result.success(user)
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging in customer", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): Result<User?> {
        val firebaseUser = auth.currentUser
        
        return if (firebaseUser != null) {
            try {
                val customerDocument = customersCollection.document(firebaseUser.uid).get().await()
                
                if (customerDocument.exists()) {
                    val data = customerDocument.data
                    
                    val user = User(
                        id = firebaseUser.uid,
                        name = data?.get("name") as? String ?: "",
                        email = data?.get("email") as? String ?: firebaseUser.email ?: "",
                        phone = data?.get("phone") as? String ?: firebaseUser.phoneNumber ?: "",
                        address = data?.get("address") as? String ?: "",
                        profileImageUrl = data?.get("profileImage") as? String ?: "",
                        membershipTier = data?.get("membershipTier") as? String ?: "Bronze",
                        rewardPoints = (data?.get("rewardPoints") as? Long)?.toInt() ?: 0
                    )
                    
                    Result.success(user)
                } else {
                    Result.success(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting current customer", e)
                Result.failure(e)
            }
        } else {
            Result.success(null)
        }
    }
    
    suspend fun updateProfile(
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        profileImage: String? = null
    ): Result<Boolean> {
        val firebaseUser = auth.currentUser ?: return Result.failure(Exception("No authenticated user"))
        
        return try {
            val updateData = hashMapOf<String, Any>()
            
            name?.let { updateData["name"] = it }
            phone?.let { updateData["phone"] = it }
            address?.let { updateData["address"] = it }
            profileImage?.let { updateData["profileImage"] = it }
            
            if (updateData.isNotEmpty()) {
                customersCollection.document(firebaseUser.uid).update(updateData).await()
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating customer profile", e)
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}