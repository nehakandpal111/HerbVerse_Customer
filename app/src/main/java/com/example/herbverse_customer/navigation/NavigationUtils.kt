package com.example.herbverse_customer.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * CompositionLocal to access the NavController from anywhere in the app
 */
val LocalNavController = compositionLocalOf<NavController> { 
    error("No NavController provided") 
}

data class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)