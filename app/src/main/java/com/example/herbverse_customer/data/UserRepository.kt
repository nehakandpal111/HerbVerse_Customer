package com.example.herbverse_customer.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.herbverse_customer.models.User
import com.example.herbverse_customer.models.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repository for managing user data, handling both authentication and user preferences
 */
class UserRepository(private val context: Context) {
    
    // Using SharedPreferences instead of DataStore to avoid import issues
    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    
    // Keys for SharedPreferences
    private object PreferencesKeys {
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_PHONE = "user_phone"
        const val USER_ADDRESS = "user_address"
        const val USER_PROFILE_IMAGE = "user_profile_image"
        const val USER_MEMBERSHIP_TIER = "user_membership_tier"
        const val USER_POINTS = "user_points"
        const val IS_LOGGED_IN = "is_logged_in"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val EMAIL_NOTIFICATIONS = "email_notifications"
        const val NEWSLETTER = "newsletter"
        const val DARK_MODE = "dark_mode"
        const val AUTH_TOKEN = "auth_token"
    }
    
    // Flow to emit preferences changes
    private val _userPreferencesFlow = MutableStateFlow(getUserPreferencesFromSharedPrefs())
    val userPreferencesFlow: Flow<UserPreferences> = _userPreferencesFlow
    
    // SharedPreferences change listener
    private val sharedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        _userPreferencesFlow.value = getUserPreferencesFromSharedPrefs()
    }
    
    init {
        // Register listener for changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
    }
    
    /**
     * Get UserPreferences from SharedPreferences
     */
    private fun getUserPreferencesFromSharedPrefs(): UserPreferences {
        return UserPreferences(
            userId = sharedPreferences.getString(PreferencesKeys.USER_ID, "") ?: "",
            userName = sharedPreferences.getString(PreferencesKeys.USER_NAME, "") ?: "",
            userEmail = sharedPreferences.getString(PreferencesKeys.USER_EMAIL, "") ?: "",
            userPhone = sharedPreferences.getString(PreferencesKeys.USER_PHONE, "") ?: "",
            userAddress = sharedPreferences.getString(PreferencesKeys.USER_ADDRESS, "") ?: "",
            userProfileImage = sharedPreferences.getString(PreferencesKeys.USER_PROFILE_IMAGE, "") ?: "",
            userMembershipTier = sharedPreferences.getString(PreferencesKeys.USER_MEMBERSHIP_TIER, "Bronze") ?: "Bronze",
            userPoints = sharedPreferences.getString(PreferencesKeys.USER_POINTS, "0")?.toIntOrNull() ?: 0,
            isLoggedIn = sharedPreferences.getBoolean(PreferencesKeys.IS_LOGGED_IN, false),
            notificationsEnabled = sharedPreferences.getBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, true),
            emailNotifications = sharedPreferences.getBoolean(PreferencesKeys.EMAIL_NOTIFICATIONS, true),
            newsletter = sharedPreferences.getBoolean(PreferencesKeys.NEWSLETTER, false),
            darkMode = sharedPreferences.getBoolean(PreferencesKeys.DARK_MODE, false)
        )
    }
    
    /**
     * Login user with email/password
     */
    suspend fun loginUser(email: String, password: String): Result<User> {
        // In a real app, this would call a backend API
        return try {
            // Simulate successful login for now
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Get a mock user
                val user = getMockUser(email)
                
                // Save user data to preferences
                saveUserToPreferences(user)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging in", e)
            Result.failure(e)
        }
    }
    
    /**
     * Register a new user
     */
    suspend fun registerUser(
        name: String, 
        email: String, 
        password: String, 
        phone: String = ""
    ): Result<User> {
        // In a real app, this would call a backend API
        return try {
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Create a new user
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email,
                    phone = phone,
                    address = "",
                    profileImageUrl = "",
                    membershipTier = "Bronze",
                    rewardPoints = 0
                )
                
                // Save user data to preferences
                saveUserToPreferences(newUser)
                
                Result.success(newUser)
            } else {
                Result.failure(Exception("Invalid registration data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Logout the current user
     */
    suspend fun logoutUser() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putBoolean(PreferencesKeys.IS_LOGGED_IN, false)
                .putString(PreferencesKeys.AUTH_TOKEN, "")
                .apply()
        }
    }
    
    /**
     * Update user profile information
     */
    suspend fun updateUserProfile(
        name: String? = null, 
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        profileImage: String? = null
    ): Result<User> {
        return try {
            withContext(Dispatchers.IO) {
                val editor = sharedPreferences.edit()
                
                name?.let { editor.putString(PreferencesKeys.USER_NAME, it) }
                email?.let { editor.putString(PreferencesKeys.USER_EMAIL, it) }
                phone?.let { editor.putString(PreferencesKeys.USER_PHONE, it) }
                address?.let { editor.putString(PreferencesKeys.USER_ADDRESS, it) }
                profileImage?.let { editor.putString(PreferencesKeys.USER_PROFILE_IMAGE, it) }
                
                editor.apply()
            }
            
            // Get updated user
            val updatedPrefs = userPreferencesFlow.first()
            Result.success(
                User(
                    id = updatedPrefs.userId,
                    name = updatedPrefs.userName,
                    email = updatedPrefs.userEmail,
                    phone = updatedPrefs.userPhone,
                    address = updatedPrefs.userAddress,
                    profileImageUrl = updatedPrefs.userProfileImage,
                    membershipTier = updatedPrefs.userMembershipTier,
                    rewardPoints = updatedPrefs.userPoints
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update notification preferences
     */
    suspend fun updateNotificationPreferences(
        notificationsEnabled: Boolean? = null,
        emailNotifications: Boolean? = null,
        newsletter: Boolean? = null
    ) {
        withContext(Dispatchers.IO) {
            val editor = sharedPreferences.edit()
            
            notificationsEnabled?.let { editor.putBoolean(PreferencesKeys.NOTIFICATIONS_ENABLED, it) }
            emailNotifications?.let { editor.putBoolean(PreferencesKeys.EMAIL_NOTIFICATIONS, it) }
            newsletter?.let { editor.putBoolean(PreferencesKeys.NEWSLETTER, it) }
            
            editor.apply()
        }
    }
    
    /**
     * Update app theme preference
     */
    suspend fun updateDarkModePreference(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putBoolean(PreferencesKeys.DARK_MODE, enabled)
                .apply()
        }
    }
    
    /**
     * Save user data to preferences
     */
    private suspend fun saveUserToPreferences(user: User) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putString(PreferencesKeys.USER_ID, user.id)
                .putString(PreferencesKeys.USER_NAME, user.name)
                .putString(PreferencesKeys.USER_EMAIL, user.email)
                .putString(PreferencesKeys.USER_PHONE, user.phone ?: "")
                .putString(PreferencesKeys.USER_ADDRESS, user.address ?: "")
                .putString(PreferencesKeys.USER_PROFILE_IMAGE, user.profileImageUrl ?: "")
                .putString(PreferencesKeys.USER_MEMBERSHIP_TIER, user.membershipTier)
                .putString(PreferencesKeys.USER_POINTS, user.rewardPoints.toString())
                .putBoolean(PreferencesKeys.IS_LOGGED_IN, true)
                .putString(PreferencesKeys.AUTH_TOKEN, "mock_token_${user.id}")
                .apply()
        }
    }
    
    /**
     * Get a mock user for testing
     */
    private fun getMockUser(email: String): User {
        return User(
            id = UUID.randomUUID().toString(),
            name = "John Doe",
            email = email,
            phone = "555-1234",
            address = "123 Herb Street, Green City",
            profileImageUrl = "",
            membershipTier = "Silver",
            rewardPoints = 850
        )
    }
    
    companion object {
        private const val TAG = "UserRepository"
    }
}