package com.example.herbverse_customer.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.herbverse_customer.HerbverseApplication
import com.example.herbverse_customer.navigation.Screen
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // State for user profile and settings
    var userName by remember { mutableStateOf("Neha") }
    var userEmail by remember { mutableStateOf("kandpalneha769@gmail.com") }
    var isLoading by remember { mutableStateOf(true) }
    var rewardPoints by remember { mutableStateOf(850) }
    var membershipTier by remember { mutableStateOf("Silver") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Notification preferences
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailNotificationsEnabled by remember { mutableStateOf(true) }
    var newsletterEnabled by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Simulate loading user data
    LaunchedEffect(Unit) {
        // In a real app, fetch user data from a repository
        // For now, simulate a network delay
        kotlinx.coroutines.delay(800)
        isLoading = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { showLogoutDialog = true }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    name = userName,
                    email = userEmail
                )
                
                // Membership card
                MembershipCard(
                    tier = membershipTier,
                    points = rewardPoints
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick action buttons
                QuickActions(
                    onOrdersClick = { navController.navigate(Screen.Orders.route) },
                    onWishlistClick = { 
    coroutineScope.launch {
        snackbarHostState.showSnackbar("Wishlist feature coming soon!")
    }
},
                    onFavoritesClick = { 
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Favorites feature coming soon!")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Full profile options
                ProfileOptions(
                    onOrdersClick = { navController.navigate(Screen.Orders.route) },
                    onAddressClick = { 
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Address management coming soon!")
                        }
                    },
                    onNotificationsClick = { 
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Notification settings enabled")
                        }
                    },
                    onSettingsClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Settings page coming soon!")
                        }
                    },
                    onAccountClick = {
                        // Navigate to account settings
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Notification preferences
                NotificationPreferences(
                    notificationsEnabled = notificationsEnabled,
                    emailNotificationsEnabled = emailNotificationsEnabled,
                    newsletterEnabled = newsletterEnabled,
                    onNotificationsToggle = { notificationsEnabled = it },
                    onEmailToggle = { emailNotificationsEnabled = it },
                    onNewsletterToggle = { newsletterEnabled = it }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // App version info
                Text(
                    text = "Herbverse v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // In real app, perform logout operations
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes, Log Out")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    name: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        FilledTonalButton(
            onClick = { /* Edit profile */ },
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Edit Profile")
        }
    }
}

@Composable
fun MembershipCard(
    tier: String,
    points: Int
) {
    val nextTier = if (tier == "Silver") "Gold" else "Platinum"
    val pointsToNext = if (tier == "Silver") 1000 - points else 2000 - points
    val progress = if (tier == "Silver") points / 1000f else (points - 1000) / 1000f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$tier Member",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (tier == "Gold") Color(0xFFFFD700) else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "$points Points",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$pointsToNext points to $nextTier",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun QuickActions(
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Orders
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onOrdersClick)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Orders",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Orders",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Wishlist
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onWishlistClick)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Wishlist",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Wishlist",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Favorites
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onFavoritesClick)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileOptions(
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        
        ProfileOptionItem(
            icon = Icons.Default.ShoppingBag,
            title = "My Orders",
            subtitle = "Track, return, or buy things again",
            onClick = onOrdersClick
        )
        
        ProfileOptionItem(
            icon = Icons.Default.AccountCircle,
            title = "Account Settings",
            subtitle = "Change password, manage linked accounts",
            onClick = onAccountClick
        )
        
        ProfileOptionItem(
            icon = Icons.Default.LocationOn,
            title = "Delivery Addresses",
            subtitle = "Manage your delivery addresses",
            onClick = onAddressClick
        )
        
        ProfileOptionItem(
            icon = Icons.Default.History,
            title = "Purchase History",
            subtitle = "View your past purchases",
            onClick = { /* Navigate to purchase history */ }
        )
        
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        
        Text(
            text = "Settings & Support",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        
        ProfileOptionItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Manage notification preferences",
            onClick = onNotificationsClick
        )
        
        ProfileOptionItem(
            icon = Icons.Default.Email,
            title = "Contact Support",
            subtitle = "Get help with your account",
            onClick = { /* Navigate to support */ }
        )
        
        ProfileOptionItem(
            icon = Icons.Default.Settings,
            title = "Settings",
            subtitle = "App preferences, language, theme",
            onClick = onSettingsClick
        )
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun NotificationPreferences(
    notificationsEnabled: Boolean,
    emailNotificationsEnabled: Boolean,
    newsletterEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    onEmailToggle: (Boolean) -> Unit,
    onNewsletterToggle: (Boolean) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notification Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            NotificationToggleItem(
                title = "Push Notifications",
                description = "Get real-time updates about orders and promotions",
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsToggle
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            NotificationToggleItem(
                title = "Email Notifications",
                description = "Receive order confirmations and updates",
                checked = emailNotificationsEnabled,
                onCheckedChange = onEmailToggle
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            NotificationToggleItem(
                title = "Newsletter",
                description = "Get weekly herb tips and special offers",
                checked = newsletterEnabled,
                onCheckedChange = onNewsletterToggle
            )
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    Herbverse_customerTheme {
        ProfileScreen(navController = rememberNavController())
    }
}