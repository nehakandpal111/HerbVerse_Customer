package com.example.herbverse_customer.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.herbverse_customer.R
import com.example.herbverse_customer.data.model.Category
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme
import com.example.herbverse_customer.ui.screens.discovery.HerbDiscoveryScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalContext
import com.example.herbverse_customer.HerbverseApplication
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.herbverse_customer.navigation.LocalNavController
import com.example.herbverse_customer.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (productId: String) -> Unit = {},
    onQuizClick: () -> Unit = {},
    onDiscoveryClick: () -> Unit = {},
    onVendorClick: (vendorId: String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val items = listOf(
        NavigationItem("Home", Icons.Default.Home),
        NavigationItem("Discover", Icons.Default.Explore),
        NavigationItem("Wishlist", Icons.Default.Favorite),
        NavigationItem("My Orders", Icons.Default.LocalShipping),
        NavigationItem("Account", Icons.Default.AccountCircle),
        NavigationItem("Settings", Icons.Default.Settings)
    )
    
    // Bottom navigation selection state
    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Initialize any required data here
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Herbverse",
                    modifier = Modifier.padding(horizontal = 28.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(Modifier.padding(horizontal = 28.dp))
                Spacer(Modifier.height(12.dp))
                
                items.forEachIndexed { index, item ->
                    if (index == 4) {
                        // Add divider before account section
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(Modifier.padding(horizontal = 28.dp))
                        Spacer(Modifier.height(8.dp))
                    }
                    
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.name) },
                        label = { Text(item.name) },
                        selected = index == selectedItem,
                        onClick = {
                            selectedItem = index
                            scope.launch { 
                                drawerState.close()
                                when (index) {
                                    0 -> navController.navigate(Screen.Home.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                    1 -> navController.navigate(Screen.Discover.route)
                                    2 -> navController.navigate(Screen.Wishlist.route)
                                    3 -> navController.navigate(Screen.Orders.route)
                                    4 -> navController.navigate(Screen.Profile.route)
                                    5 -> { /* Navigate to settings */ }
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                
                // Sign out option at the bottom
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(Modifier.padding(horizontal = 28.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Sign Out") },
                    label = { Text("Sign Out") },
                    selected = false,
                    onClick = { /* Handle sign out */ },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(12.dp))
            }
        },
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(text = "Herbverse") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = { 
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            // Cart icon with badge
                            val context = LocalContext.current
                            val cartViewModel = remember { (context.applicationContext as HerbverseApplication).cartViewModel }
                            
                            IconButton(onClick = onCartClick) {
                                Box {
                                    Icon(
                                        Icons.Default.ShoppingCart, 
                                        contentDescription = "Cart",
                                        tint = Color.White,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    
                                    // Show badge (would show cart count with real data)
                                    if (true) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .align(Alignment.TopEnd)
                                                .background(Color.Red, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "1",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation(
                        selectedTab = selectedBottomTab,
                        onTabSelected = { selectedBottomTab = it }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeContent(
                        modifier = Modifier.fillMaxSize(),
                        onProductClick = onProductClick,
                        onQuizClick = onQuizClick,
                        onDiscoveryClick = onDiscoveryClick
                    )
                }
            }
        }
    )
}

data class NavigationItem(val name: String, val icon: ImageVector)

@Composable
fun BottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val navController = LocalNavController.current
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { 
                onTabSelected(0)
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Orders") },
            label = { Text("Orders") },
            selected = selectedTab == 1,
            onClick = { 
                onTabSelected(1)
                navController.navigate(Screen.Orders.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { 
                Box {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") 
                    // Show badge if there are items in cart
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            },
            label = { Text("Cart") },
            selected = selectedTab == 2,
            onClick = { 
                onTabSelected(2)
                navController.navigate(Screen.Cart.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { 
                onTabSelected(3)
                navController.navigate(Screen.Profile.route)
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onProductClick: (productId: String) -> Unit = {},
    onQuizClick: () -> Unit = {},
    onDiscoveryClick: () -> Unit = {},
    onVendorClick: (vendorId: String) -> Unit = {}
) {
    // Use remember to avoid recomposition issues
    val products = remember { sampleProducts.plus(globalHerbs) }
    val categories = remember { sampleCategories }
    val pairings = remember { samplePairings }
    
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            SearchBar()
        }

        // Popular Herbs Section first (moved up)
        item {
            SectionHeader(title = "Popular Herbs")
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(products.chunked(2)) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (product in row) {
                    ProductCard(
                        product = product,
                        modifier = Modifier.weight(1f),
                        onProductClick = onProductClick
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            BannerSection(onDiscoveryClick = onDiscoveryClick)
        }

        item {
            SectionHeader(title = "Categories")
            Spacer(modifier = Modifier.height(8.dp))
            CategoriesRow(categories = categories)
        }

        item {
            HerbQuizSection(onQuizClick)
        }
        
        item {
            VendorStoriesSection(onVendorClick)
        }
        
        item {
            SustainabilitySection()
        }
        
        // Add spacing at bottom for the navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Product>>(emptyList()) }
    val context = LocalContext.current
    val navController = LocalNavController.current
    
    TextField(
        value = text,
        onValueChange = { newText -> 
            text = newText
        },
        placeholder = { Text("Search for herbs...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = { 
                    text = ""
                    isSearchActive = false
                    searchResults = emptyList()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                navController.navigate(Screen.Search.route)
            },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
            focusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        enabled = false // Make it not editable since we navigate to SearchScreen
    )
}

// Function to search herbs based on query
private fun searchHerbs(query: String): List<Product> {
    val allProducts = sampleProducts + globalHerbs
    return allProducts.filter { 
        it.name.contains(query, ignoreCase = true) || 
        it.shortDescription.contains(query, ignoreCase = true) 
    }
}

@Composable
fun BannerSection(onDiscoveryClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Use the banner image from resources instead of gradient
            Image(
                painter = painterResource(R.drawable.banner_fresh_herbs),
                contentDescription = "Fresh Herbs Collection",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Add semi-transparent overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Fresh Herbs Collection",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "100% Organic & Natural",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDiscoveryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1B5E20)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Discover Herbs")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    val navController = LocalNavController.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { 
                // View all products
                navController.navigate(Screen.Browse.route)
            }
        ) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View all",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategoriesRow(categories: List<Category>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryItem(category = category)
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    val navController = LocalNavController.current
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp)
            .clickable { 
                // Navigate to product browse screen with category filter
                navController.navigate(Screen.Browse.route)
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when(category.id) {
                "1" -> R.drawable.category_medicinal
                "2" -> R.drawable.category_culinary
                "3" -> R.drawable.category_aromatic
                "4" -> R.drawable.category_essential_oils
                "5" -> R.drawable.category_dried
                else -> R.drawable.category_default
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Add semi-transparent overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun HerbPairingSuggestions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        SectionHeader(title = "Pairing Ideas")
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(samplePairings) { pairing ->
                PairingCard(pairing = pairing)
            }
        }
    }
}

@Composable
fun PairingCard(pairing: HerbPairing) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { /* Navigate to pairing details */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when("${pairing.firstHerb}_${pairing.secondHerb}".lowercase().replace(" ", "_")) {
                "basil_thyme" -> R.drawable.pairing_basil_thyme
                "mint_lavender" -> R.drawable.pairing_mint_lavender
                "rosemary_sage" -> R.drawable.pairing_rosemary_sage
                "chamomile_lemon_balm" -> R.drawable.pairing_chamomile_lemonbalm
                else -> R.drawable.pairing_default
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = "${pairing.firstHerb} and ${pairing.secondHerb}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Add gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 300f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "${pairing.firstHerb} + ${pairing.secondHerb}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = pairing.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onProductClick: (productId: String) -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { 
                // Log the click for debugging
                android.util.Log.d("ProductCard", "Product clicked: ${product.id}")
                onProductClick(product.id) 
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                val imageRes = when(product.id) {
                    "1" -> R.drawable.herb_lavender
                    "2" -> R.drawable.herb_basil
                    "3" -> R.drawable.herb_chamomile
                    "4" -> R.drawable.herb_rosemary
                    "5" -> R.drawable.herb_peppermint
                    "6" -> R.drawable.herb_echinacea
                    else -> R.drawable.herb_default
                }
                
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Card(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { /* Add to cart */ },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Add to cart",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HerbQuizSection(onQuizClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.quiz_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Add overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f))
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Discover Your Perfect Herb",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Take our quick quiz to find the perfect herbs for your needs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onQuizClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take the Quiz")
                }
            }
        }
    }
}

@Composable
fun SustainabilitySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sustainability_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Add overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f))
            )
            
            Column {
                Text(
                    text = "Our Sustainability Promise",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "We're committed to sustainable farming practices that protect our planet and produce the highest quality herbs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "100%",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Organic",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Zero",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pesticides",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Local",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sourcing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VendorStoriesSection(onVendorClick: (vendorId: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        SectionHeader(title = "Meet Our Vendors")
        Spacer(modifier = Modifier.height(8.dp))

        var showVendorDetails by remember { mutableStateOf<String?>(null) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { onVendorClick("1") },
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(R.drawable.vendor_willow_creek),
                    contentDescription = "Willow Creek Farms",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Add gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0f,
                                endY = 300f
                            )
                        )
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Willow Creek Farms",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Fourth-generation organic herb farmers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun HerbDiscoveryExperience(onDismiss: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedUsage by remember { mutableStateOf<String?>(null) }
    var selectedBenefit by remember { mutableStateOf<String?>(null) }
    var isARMode by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Prevent clicks passing through */ }
    ) {
        when {
            isARMode -> {
                ARHerbVisualizationScreen(
                    selectedUsage = selectedUsage ?: "",
                    selectedBenefit = selectedBenefit ?: "",
                    onClose = onDismiss
                )
            }
            currentStep == 0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Discover Your Perfect Herbs",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "How do you plan to use your herbs?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val usageOptions = listOf("Cooking", "Medicinal", "Tea", "Aromatherapy", "Gardening")
                    usageOptions.forEach { usage ->
                        Button(
                            onClick = { 
                                selectedUsage = usage
                                currentStep = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(usage)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Cancel")
                    }
                }
            }
            currentStep == 1 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "What benefits are you looking for?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val benefitOptions = when(selectedUsage) {
                        "Cooking" -> listOf("Flavor Enhancement", "Italian Cuisine", "Asian Fusion", "Garnishing", "BBQ & Grilling")
                        "Medicinal" -> listOf("Relaxation", "Immune Support", "Digestive Health", "Respiratory Relief", "Sleep Aid")
                        "Tea" -> listOf("Relaxation", "Energy Boost", "Digestion", "Detox", "Flavor")
                        "Aromatherapy" -> listOf("Relaxation", "Focus", "Energy", "Sleep Aid", "Mood Lifting")
                        "Gardening" -> listOf("Easy to Grow", "Pest Control", "Companion Planting", "Indoor Growing", "Drought Resistant")
                        else -> listOf("General Wellbeing", "Flavor", "Aroma", "Decoration")
                    }
                    
                    benefitOptions.forEach { benefit ->
                        Button(
                            onClick = { 
                                selectedBenefit = benefit 
                                isARMode = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(benefit)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = { currentStep = 0 }) {
                            Text("Back")
                        }
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ARHerbVisualizationScreen(
    selectedUsage: String,
    selectedBenefit: String,
    onClose: () -> Unit
) {
    var currentHerbIndex by remember { mutableStateOf(0) }
    var isPlacing by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    
    // Determine recommended herbs based on selections
    val recommendedHerbs = remember(selectedUsage, selectedBenefit) {
        when {
            selectedUsage == "Cooking" && selectedBenefit == "Italian Cuisine" -> 
                listOf("Basil", "Oregano", "Rosemary", "Thyme", "Sage")
            selectedUsage == "Medicinal" && selectedBenefit == "Relaxation" ->
                listOf("Lavender", "Chamomile", "Lemon Balm", "Holy Basil", "Passionflower")
            selectedUsage == "Tea" && selectedBenefit == "Sleep Aid" ->
                listOf("Chamomile", "Lavender", "Valerian Root", "Lemon Balm", "Passionflower")
            selectedUsage == "Aromatherapy" && selectedBenefit == "Focus" ->
                listOf("Rosemary", "Peppermint", "Basil", "Sage", "Lemon")
            // Add more combinations as needed
            else -> listOf("Basil", "Mint", "Rosemary", "Lavender", "Thyme")
        }
    }
    
    val currentHerb = recommendedHerbs[currentHerbIndex]
    
    Box(modifier = Modifier.fillMaxSize()) {
        // AR Camera Preview Background (simulated with an image for now)
        Image(
            painter = painterResource(R.drawable.banner_fresh_herbs),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f  // Dimmed to show AR overlay effect
        )
        
        // AR Elements Overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isPlacing) {
                // Show 3D herb model placeholder with placement guides
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 3D Model Placeholder
                    val herbImageRes = when(currentHerb) {
                        "Basil" -> R.drawable.herb_basil
                        "Lavender" -> R.drawable.herb_lavender 
                        "Chamomile" -> R.drawable.herb_chamomile
                        "Rosemary" -> R.drawable.herb_rosemary
                        "Peppermint" -> R.drawable.herb_peppermint
                        else -> R.drawable.herb_default
                    }
                    
                    Image(
                        painter = painterResource(herbImageRes),
                        contentDescription = currentHerb,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Text(
                        "Tap to place $currentHerb in your space",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(8.dp)
                    )
                }
            }
        }
        
        // AR Controls and Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                "AR Herb Experience",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Visualize $currentHerb in your space",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        // AR Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    currentHerbIndex = (currentHerbIndex - 1 + recommendedHerbs.size) % recommendedHerbs.size
                    isPlacing = false
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Herb",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Button(
                onClick = { isPlacing = !isPlacing },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(if (isPlacing) "Cancel Placement" else "Place Herb")
            }
            
            IconButton(
                onClick = {
                    currentHerbIndex = (currentHerbIndex + 1) % recommendedHerbs.size
                    isPlacing = false
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Herb",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Info Panel (conditionally shown)
        if (showInfo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showInfo = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.Center)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            currentHerb,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Herb information based on the selected herb
                        val herbInfo = when(currentHerb) {
                            "Basil" -> "Sweet basil is a culinary herb used in many cuisines. It has a strong, sweet smell and a distinctive taste."
                            "Lavender" -> "Lavender is known for its calming properties and sweet floral scent, often used in aromatherapy."
                            "Chamomile" -> "Chamomile is commonly used to make a calming, sleep-inducing tea that helps with relaxation."
                            "Rosemary" -> "Rosemary has needle-like leaves with a strong pine-like fragrance, perfect for cooking and cognitive benefits."
                            "Peppermint" -> "Peppermint has a fresh, cooling sensation and is used for digestive issues and flavoring."
                            else -> "This herb has many culinary and medicinal properties that make it valuable in your collection."
                        }
                        
                        Text(
                            herbInfo,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showInfo = false }
                            ) {
                                Text("Back to AR")
                            }
                            
                            Button(
                                onClick = {
                                    // In a real app, this would add to cart
                                    showInfo = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Add to Cart")
                            }
                        }
                    }
                }
            }
        }
        
        // Action buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { showInfo = !showInfo },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Herb Information",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close AR Experience",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun HerbARPlacementGuide() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .border(1.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw crosshair
            val center = Offset(size.width / 2, size.height / 2)
            val lineLength = 20f
            
            drawLine(
                color = Color.White,
                start = Offset(center.x - lineLength, center.y),
                end = Offset(center.x + lineLength, center.y),
                strokeWidth = 2f
            )
            
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y - lineLength),
                end = Offset(center.x, center.y + lineLength),
                strokeWidth = 2f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Herbverse_customerTheme {
        HomeScreen(
            onProductClick = {},
            onQuizClick = {},
            onDiscoveryClick = {},
            onCartClick = {}
        )
    }
}

// Sample data models
data class HerbPairing(
    val firstHerb: String,
    val secondHerb: String,
    val description: String
)

// Sample data
private val sampleCategories = listOf(
    Category("1", "Medicinal"),
    Category("2", "Culinary"),
    Category("3", "Aromatic"),
    Category("4", "Essential Oils"),
    Category("5", "Dried")
)

private val sampleProducts = listOf(
    Product(
        id = "1",
        name = "Lavender",
        shortDescription = "Fresh aromatic herb with soothing properties",
        fullDescription = "Lavender is well known for its fragrance and medicinal properties.",
        price = 9.99,
        stock = 50,
        categoryId = "3",
        imageUrl = ""
    ),
    Product(
        id = "2",
        name = "Basil",
        shortDescription = "Fresh culinary herb with a sweet aroma",
        fullDescription = "Basil is a culinary herb of the family Lamiaceae.",
        price = 4.99,
        stock = 100,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "3",
        name = "Chamomile",
        shortDescription = "Herbal remedy for relaxation and sleep",
        fullDescription = "Chamomile is known for its soothing properties.",
        price = 7.99,
        stock = 80,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "4",
        name = "Rosemary",
        shortDescription = "Fragrant herb with needle-like leaves",
        fullDescription = "Rosemary is a fragrant evergreen herb with needle-like leaves.",
        price = 5.99,
        stock = 70,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "5",
        name = "Peppermint",
        shortDescription = "Cooling herb perfect for teas and remedies",
        fullDescription = "Peppermint is a hybrid mint, a cross between watermint and spearmint.",
        price = 6.99,
        stock = 90,
        categoryId = "1",
        imageUrl = ""
    )
)

// Global list of more herb varieties from around the world
private val globalHerbs = listOf(
    Product(
        id = "6",
        name = "Echinacea",
        shortDescription = "Powerful immune system booster herb",
        fullDescription = "Echinacea is popular for its immune-boosting and anti-inflammatory properties.",
        price = 8.99,
        stock = 60,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "7",
        name = "Turmeric",
        shortDescription = "Ancient golden spice with medicinal properties",
        fullDescription = "Turmeric has been used in India for thousands of years as both a spice and medicinal herb.",
        price = 7.49,
        stock = 75,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "8",
        name = "Ginseng",
        shortDescription = "Traditional Asian herb for energy and vitality",
        fullDescription = "Ginseng is a popular herb in traditional Chinese medicine known for boosting energy.",
        price = 12.99,
        stock = 40,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "9",
        name = "Cilantro",
        shortDescription = "Fresh herb essential in Latin and Asian cuisines",
        fullDescription = "Cilantro (Coriander) is widely used in cuisines around the world for its distinctive flavor.",
        price = 3.99,
        stock = 110,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "10",
        name = "Lemongrass",
        shortDescription = "Citrusy herb popular in Southeast Asian dishes",
        fullDescription = "Lemongrass is a tropical herb with a lemony aroma and citrus flavor common in Thai cuisine.",
        price = 5.49,
        stock = 65,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "11",
        name = "Sage",
        shortDescription = "Aromatic herb with earthy flavor and healing properties",
        fullDescription = "Sage has been used for centuries for both culinary and medicinal purposes.",
        price = 4.49,
        stock = 85,
        categoryId = "3",
        imageUrl = ""
    ),
    Product(
        id = "12",
        name = "Holy Basil (Tulsi)",
        shortDescription = "Sacred herb in Indian tradition with adaptogenic properties",
        fullDescription = "Holy Basil is considered sacred in India and is known for its healing properties.",
        price = 8.99,
        stock = 55,
        categoryId = "3",
        imageUrl = ""
    ),
    Product(
        id = "13",
        name = "Fennel",
        shortDescription = "Licorice-flavored herb used in Mediterranean cooking",
        fullDescription = "Fennel has a distinctive anise flavor and is used in many Mediterranean dishes.",
        price = 4.99,
        stock = 70,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "14",
        name = "Thyme",
        shortDescription = "Versatile herb with earthy, slightly minty flavor",
        fullDescription = "Thyme is one of the most versatile herbs, with various cultivars and flavors.",
        price = 3.99,
        stock = 90,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "15",
        name = "Valerian",
        shortDescription = "Natural sleep aid and anxiety relieving herb",
        fullDescription = "Valerian root has been used since ancient times to promote tranquility and improve sleep.",
        price = 9.99,
        stock = 45,
        categoryId = "1",
        imageUrl = ""
    )
)

private val samplePairings = listOf(
    HerbPairing(
        firstHerb = "Basil",
        secondHerb = "Thyme",
        description = "Perfect for Italian dishes and Mediterranean cuisine"
    ),
    HerbPairing(
        firstHerb = "Mint",
        secondHerb = "Lavender",
        description = "Ideal for refreshing summer teas and cocktails"
    ),
    HerbPairing(
        firstHerb = "Rosemary",
        secondHerb = "Sage",
        description = "Great combination for roasted meats and savory dishes"
    ),
    HerbPairing(
        firstHerb = "Chamomile",
        secondHerb = "Lemon Balm",
        description = "Soothing evening tea blend for relaxation"
    )
)