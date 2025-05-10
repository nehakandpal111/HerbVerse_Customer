# Herbverse Vendor App Implementation Plan

## Overview

The Herbverse Vendor app will be a separate Android application that allows herb vendors to:

1. Manage their product listings (add, edit, delete)
2. Update farm information and stories
3. Process orders from customers
4. View sales analytics

Both apps (customer and vendor) will share the same Firebase backend for real-time synchronization.

## Project Structure

```
com.example.herbverse_vendor/
├── auth/                      # Authentication components
│   ├── AuthRepository.kt      # Firebase Auth operations
│   ├── AuthViewModel.kt       # Auth state management
│   └── ui/
│       └── LoginScreen.kt     # Email link sign-in UI
│
├── data/                      # Data layer
│   ├── model/                 # Data models
│   │   ├── Product.kt
│   │   ├── Order.kt
│   │   ├── Vendor.kt
│   │   └── FarmStory.kt
│   ├── repository/            # Data repositories
│   │   ├── ProductRepository.kt
│   │   ├── OrderRepository.kt
│   │   └── VendorRepository.kt
│   └── remote/                # Firebase implementations
│       └── FirestoreService.kt
│
├── ui/                        # UI layer
│   ├── MainActivity.kt        # Entry point
│   ├── theme/                 # UI theme
│   └── screens/               # App screens
│       ├── products/          # Product management screens
│       │   ├── ProductListScreen.kt
│       │   ├── AddProductScreen.kt
│       │   └── EditProductScreen.kt
│       ├── orders/            # Order management screens
│       │   ├── OrderListScreen.kt
│       │   └── OrderDetailScreen.kt
│       ├── profile/           # Vendor profile screens
│       │   ├── ProfileScreen.kt
│       │   └── FarmStoryEditor.kt
│       └── dashboard/         # Analytics dashboard
│           └── DashboardScreen.kt
└── util/                      # Utilities
    └── ImageUploadUtil.kt     # Image upload helpers
```

## Firebase Configuration

The vendor app will be registered to the same Firebase project as the customer app, but with
specific security rules to:

1. Allow vendors to only modify their own products
2. Allow vendors to only view orders assigned to them
3. Allow vendors to only edit their own profile information

## Data Models

### Product

```kotlin
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val category: String = "",
    val imageUrl: String = "",
    val vendorId: String = "",
    val isOrganic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Order

```kotlin
data class Order(
    val id: String = "",
    val customerName: String = "",
    val customerId: String = "",
    val items: List<OrderItem> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val total: Double = 0.0,
    val vendorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val shippingAddress: Address = Address()
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}
```

### Vendor Profile

```kotlin
data class Vendor(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val farmName: String = "",
    val farmDescription: String = "",
    val farmStory: String = "",
    val farmImages: List<String> = emptyList(),
    val location: GeoPoint? = null,
    val address: Address = Address(),
    val rating: Double = 0.0,
    val isOrganic: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

## Key Screens

### Product Management

1. **Product List Screen**: Shows all products by the vendor with options to add, edit, or delete.
2. **Add/Edit Product Screen**: Form for creating or editing product details, including photo
   uploads.

### Order Management

1. **Order List Screen**: Lists all orders with filtering by status.
2. **Order Detail Screen**: Shows order details and allows status updates.

### Profile Management

1. **Profile Screen**: Shows and allows editing of vendor profile information.
2. **Farm Story Editor**: Rich text editor for creating compelling farm stories.

### Dashboard

1. **Analytics Dashboard**: Shows sales data, popular products, and performance metrics.

## Key Features for Vendor-Customer Integration

1. **Real-time Order Updates**: When a vendor updates an order status, the customer app sees it
   immediately.
2. **Product Updates**: When a vendor adds or edits a product, it's instantly available to
   customers.
3. **Farm Story Broadcasting**: Rich vendor stories are visible to customers in the customer app.
4. **Stock Management**: Stock quantities automatically update across both apps.

## Technical Implementation Steps

1. Create new Android project with package `com.example.herbverse_vendor`
2. Add Firebase SDK and configure with the same Firebase project
3. Copy and adapt the authentication flow from the customer app
4. Implement Firestore repositories for products, orders, and vendor profiles
5. Create the UI components for each screen
6. Implement image upload functionality for products and farm stories
7. Add real-time listeners for orders to provide immediate notifications
8. Implement analytics tracking for sales and performance data

## Security Considerations

1. **Vendor Authentication**: Only authenticated vendors can access the app
2. **Data Isolation**: Vendors can only see and manage their own data
3. **Order Privacy**: Vendors can only access orders assigned to them
4. **Image Upload Security**: Secure upload process for product and farm images
5. **Notification Privacy**: Ensure notifications only contain appropriate information