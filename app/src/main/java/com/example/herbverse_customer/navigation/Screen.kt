package com.example.herbverse_customer.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Browse : Screen("browse")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Orders : Screen("orders")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation")
    object TrackOrder : Screen("track_order/{orderId}") {
        fun createRoute(orderId: String) = "track_order/$orderId"
    }
    object Search : Screen("search")
    object Discover : Screen("discover")
    object Wishlist : Screen("wishlist")
    object Quiz : Screen("quiz")
}