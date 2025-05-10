package com.example.herbverse_customer.models

/**
 * User data model representing a registered user in the system
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val membershipTier: String = "Bronze", // Bronze, Silver, Gold, Platinum
    val rewardPoints: Int = 0
)

/**
 * User preferences to be stored in DataStore
 */
data class UserPreferences(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userAddress: String = "",
    val userProfileImage: String = "",
    val userMembershipTier: String = "Bronze",
    val userPoints: Int = 0,
    val isLoggedIn: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val newsletter: Boolean = false,
    val darkMode: Boolean = false
)