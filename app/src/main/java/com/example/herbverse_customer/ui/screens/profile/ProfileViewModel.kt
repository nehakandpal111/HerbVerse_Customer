package com.example.herbverse_customer.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.herbverse_customer.data.UserRepository
import com.example.herbverse_customer.models.User
import com.example.herbverse_customer.models.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing profile screen data and interactions
 */
class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // UI state for profile screen
    data class ProfileUiState(
        val isLoading: Boolean = true,
        val user: User? = null,
        val userPreferences: UserPreferences = UserPreferences(),
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    // User preferences as StateFlow
    val userPreferences = userRepository.userPreferencesFlow
        .catch { e ->
            _uiState.update { 
                it.copy(errorMessage = "Error loading preferences: ${e.message}") 
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )
        
    init {
        loadProfileData()
    }
    
    /**
     * Load user profile data from repository
     */
    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                // In a real app, this would get the current user data from a backend
                // Here we simulate with userPreferences data
                userRepository.userPreferencesFlow.collect { preferences ->
                    if (preferences.isLoggedIn && preferences.userId.isNotEmpty()) {
                        val user = User(
                            id = preferences.userId,
                            name = preferences.userName,
                            email = preferences.userEmail,
                            phone = preferences.userPhone,
                            address = preferences.userAddress,
                            profileImageUrl = preferences.userProfileImage,
                            membershipTier = preferences.userMembershipTier,
                            rewardPoints = preferences.userPoints
                        )
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                user = user,
                                userPreferences = preferences,
                                errorMessage = null
                            ) 
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                user = null,
                                userPreferences = preferences
                            ) 
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading profile: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Update user profile information
     */
    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        profileImage: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val result = userRepository.updateUserProfile(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    profileImage = profileImage
                )
                
                result.onSuccess { user ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            user = user,
                            errorMessage = null
                        )
                    }
                }.onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to update profile: ${e.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Update notification preferences
     */
    fun updateNotificationPreferences(
        notificationsEnabled: Boolean? = null,
        emailNotifications: Boolean? = null,
        newsletter: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                userRepository.updateNotificationPreferences(
                    notificationsEnabled = notificationsEnabled,
                    emailNotifications = emailNotifications,
                    newsletter = newsletter
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(errorMessage = "Failed to update notification preferences: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Toggle dark mode preference
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.updateDarkModePreference(enabled)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(errorMessage = "Failed to update theme preference: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Log out the current user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logoutUser()
                
                // Clear local state
                _uiState.update { 
                    it.copy(
                        user = null,
                        userPreferences = UserPreferences(),
                        errorMessage = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(errorMessage = "Failed to logout: ${e.message}") 
                }
            }
        }
    }
}