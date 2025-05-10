package com.example.herbverse_customer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.herbverse_customer.auth.AuthViewModel
import com.example.herbverse_customer.auth.ui.LoginScreen
import com.example.herbverse_customer.ui.screens.browse.ProductBrowseScreen
import com.example.herbverse_customer.ui.screens.home.HomeScreen
import com.example.herbverse_customer.ui.screens.product.ProductDetailScreen
import com.example.herbverse_customer.ui.screens.cart.CartScreen
import com.example.herbverse_customer.ui.screens.orders.TrackOrderScreen
import com.example.herbverse_customer.ui.screens.search.SearchScreen
import com.example.herbverse_customer.ui.screens.cart.CheckoutScreen
import com.example.herbverse_customer.ui.screens.discovery.HerbWorldMapScreen
import com.example.herbverse_customer.ui.screens.wishlist.WishlistScreen
import com.example.herbverse_customer.ui.screens.vendor.VendorDetailScreen
import com.example.herbverse_customer.navigation.Screen
import com.example.herbverse_customer.ui.screens.quiz.HerbQuizScreen
import com.example.herbverse_customer.ui.screens.profile.ProfileScreen
import com.example.herbverse_customer.ui.screens.orders.OrdersScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
    isAuthenticated: Boolean = false,
    authViewModel: AuthViewModel
) {
    val actions = remember(navController) { NavigationActions(navController) }
    
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Home.route else startDestination,
            modifier = modifier
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = actions.navigateToHome
                )
            }
            
            composable(Screen.Home.route) {
                HomeScreen(
                    onProductClick = { productId -> actions.navigateToProductDetail(productId) },
                    onQuizClick = { actions.navigateToQuiz() },
                    onDiscoveryClick = { actions.navigateToDiscover() },
                    onVendorClick = { vendorId -> actions.navigateToVendorDetail(vendorId) },
                    onCartClick = { actions.navigateToCart() }
                )
            }
            
            composable(Screen.Browse.route) {
                ProductBrowseScreen(
                    onProductClick = { productId -> actions.navigateToProductDetail(productId) }
                )
            }
            
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onAddToCart = { product, quantity -> 
                        // Just stay on the screen, cart is already updated
                    },
                    onCheckout = { actions.navigateToCart() }
                )
            }

            composable(
                route = "vendorDetail/{vendorId}",
                arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
                VendorDetailScreen(
                    vendorId = vendorId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    onBackClick = { navController.popBackStack() },
                    onCheckoutClick = { actions.navigateToCheckout() }
                )
            }
            
            composable(
                route = Screen.TrackOrder.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                TrackOrderScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onProductClick = { productId -> actions.navigateToProductDetail(productId) }
                )
            }
            
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Discover.route) {
                HerbWorldMapScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    navController = navController,
                    onBackClick = { navController.popBackStack() },
                    onProductClick = { productId -> actions.navigateToProductDetail(productId) }
                )
            }
            
            composable(Screen.Quiz.route) {
                HerbQuizScreen(
                    onBackClick = { navController.popBackStack() },
                    onProductRecommended = { productId -> actions.navigateToProductDetail(productId) }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            
            composable(Screen.Orders.route) {
                OrdersScreen(navController = navController)
            }
        }
    }
}

/**
 * Navigation actions for the app
 */
class NavigationActions(private val navController: NavHostController) {
    
    val navigateToHome: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            // Clear the back stack so users can't go back to login
            popUpTo(Screen.Login.route) { inclusive = true }
        }
    }
    
    val navigateToLogin: () -> Unit = {
        navController.navigate(Screen.Login.route) {
            // Clear the back stack when logging out
            popUpTo(0) { inclusive = true }
        }
    }
    
    val navigateToBrowse: () -> Unit = {
        navController.navigate(Screen.Browse.route)
    }
    
    fun navigateToProductDetail(productId: String) {
        navController.navigate(Screen.ProductDetail.createRoute(productId))
    }
    
    val navigateToCart: () -> Unit = {
        navController.navigate(Screen.Cart.route)
    }
    
    val navigateToCheckout: () -> Unit = {
        navController.navigate(Screen.Checkout.route)
    }
    
    fun navigateToTrackOrder(orderId: String) {
        navController.navigate(Screen.TrackOrder.createRoute(orderId))
    }
    
    val navigateToProfile: () -> Unit = {
        navController.navigate(Screen.Profile.route)
    }
    
    val navigateToDiscover: () -> Unit = {
        navController.navigate(Screen.Discover.route)
    }
    
    val navigateToTrack: () -> Unit = {
        // Use a default order ID for demonstration
        navController.navigate(Screen.TrackOrder.createRoute("default-order-123"))
    }
    
    fun navigateToVendorDetail(vendorId: String) {
        navController.navigate("vendorDetail/${vendorId}")
    }
    
    val navigateToQuiz: () -> Unit = {
        navController.navigate(Screen.Quiz.route)
    }
}