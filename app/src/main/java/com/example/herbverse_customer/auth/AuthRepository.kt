package com.example.herbverse_customer.auth

import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Repository for handling authentication-related operations
 */
class AuthRepository(private val context: Context) {
    private val TAG = "AuthRepository"
    
    // We'll use this flag to simulate auth state
    private var isAuthenticated = false
    private var currentUserEmail: String? = null
    
    companion object {
        const val PREFS_NAME = "herbverse_auth_prefs"
        const val EMAIL_KEY = "email_for_link"
        
        // For testing purposes, any of these codes will work
        private val VALID_TEST_CODES = setOf("123456", "000000", "111111")
    }

    /**
     * Sign in with a verification code
     */
    fun signInWithCode(email: String, code: String, onComplete: (Any?, Exception?) -> Unit) {
        // For testing, accept any valid test code
        if (VALID_TEST_CODES.contains(code)) {
            Log.d(TAG, "Mock: User signed in with code: $email")
            isAuthenticated = true
            currentUserEmail = email
            onComplete(MockUser(email), null)
        } else {
            Log.d(TAG, "Invalid code provided")
            onComplete(null, Exception("Invalid verification code"))
        }
    }
    
    /**
     * Save email to SharedPreferences for retrieval when completing sign-in
     */
    fun saveEmailToPrefs(email: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(EMAIL_KEY, email)
            .apply()
    }
    
    /**
     * Get email from SharedPreferences
     */
    fun getEmailFromPrefs(): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(EMAIL_KEY, null)
    }

    /**
     * Check if user is currently signed in
     */
    fun isUserSignedIn(): Boolean {
        return isAuthenticated
    }

    /**
     * Get the current signed-in user
     */
    fun getCurrentUser(): MockUser? {
        return if (isAuthenticated && currentUserEmail != null) {
            MockUser(currentUserEmail!!)
        } else {
            null
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        isAuthenticated = false
        currentUserEmail = null
        Log.d(TAG, "User signed out")
    }
    
    /**
     * Mock user class to substitute for FirebaseUser
     */
    class MockUser(val email: String) {
        val uid: String = email.hashCode().toString()
        
        override fun toString(): String {
            return "MockUser(email=$email, uid=$uid)"
        }
    }
    
    // Legacy methods kept for compatibility
    fun sendSignInLink(email: String, onComplete: (Boolean, Exception?) -> Unit) {
        Log.d(TAG, "Mock: This method is deprecated. Use signInWithCode instead.")
        onComplete(false, null)
    }
    
    fun signInWithEmailLink(email: String, emailLink: String, onComplete: (Any?, Exception?) -> Unit) {
        Log.d(TAG, "Mock: This method is deprecated. Use signInWithCode instead.")
        onComplete(null, Exception("This method is deprecated"))
    }
    
    fun isSignInWithEmailLink(intent: Intent): Boolean {
        return false
    }
    
    fun getSignInLinkFromIntent(intent: Intent): String? {
        return null
    }
}