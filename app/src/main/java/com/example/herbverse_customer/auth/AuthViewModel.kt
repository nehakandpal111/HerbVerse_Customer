package com.example.herbverse_customer.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AuthViewModel"
    private val context: Context = application

    sealed class AuthState {
        object UNAUTHENTICATED : AuthState()
        object LOADING : AuthState()
        object CODE_SENT : AuthState()
        object AUTHENTICATED : AuthState()
        data class ERROR(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.UNAUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Helper method to check if user can auto login
    fun checkAutoLogin(): Boolean {
        // Always return false to ensure login screen is shown
        return false
    }

    // Login with email and password
    fun login(email: String, password: String, rememberMe: Boolean = false) {
        if (email.isBlank()) {
            _error.value = "Please enter your email"
            _authState.value = AuthState.ERROR("Please enter your email")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.LOADING

                // Simulate network call
                delay(1500)

                // In a real app, this would be a call to your authentication API
                if (password == "password" || password == "123456" || email == "kandpalneha769@gmail.com") {
                    // Store credentials if rememberMe is checked
                    if (rememberMe) {
                        val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit()
                            .putString("user_name", "Neha")
                            .putString("saved_email", "kandpalneha769@gmail.com")
                            .putString("auth_token", "sample_token_${System.currentTimeMillis()}")
                            .apply()
                    }

                    // Update auth state to success
                    _authState.value = AuthState.AUTHENTICATED
                } else {
                    _error.value = "Invalid email or password"
                    _authState.value = AuthState.ERROR("Invalid email or password")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _error.value = e.message ?: "Authentication failed"
                _authState.value = AuthState.ERROR(e.message ?: "Authentication failed")
            }
        }
    }

    // Sign up with email and password
    fun signUp(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _error.value = "Please fill in all fields"
            _authState.value = AuthState.ERROR("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.LOADING

                // Simulate network call
                delay(1500)

                // In a real app, this would register the user with your authentication service
                // Store user info for demo
                val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit()
                    .putString("user_name", "Neha")
                    .putString("saved_email", "kandpalneha769@gmail.com")
                    .putString("auth_token", "sample_token_${System.currentTimeMillis()}")
                    .apply()

                // Update auth state to success
                _authState.value = AuthState.AUTHENTICATED

            } catch (e: Exception) {
                Log.e(TAG, "Signup error", e)
                _error.value = e.message ?: "Registration failed"
                _authState.value = AuthState.ERROR(e.message ?: "Registration failed")
            }
        }
    }

    // Demo login that bypasses authentication
    fun demoLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.LOADING

            // Simulate network call
            delay(800)

            // Store demo user info
            val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putString("user_name", "Neha")
                .putString("saved_email", "kandpalneha769@gmail.com")
                .putString("auth_token", "demo_token_${System.currentTimeMillis()}")
                .apply()

            _authState.value = AuthState.AUTHENTICATED
        }
    }

    // Sign out the user
    fun signOut() {
        viewModelScope.launch {
            // Clear saved authentication data
            val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()

            // Reset state to unauthenticated
            _error.value = null
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }
}