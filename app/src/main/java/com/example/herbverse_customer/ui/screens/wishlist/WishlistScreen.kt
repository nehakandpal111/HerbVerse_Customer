package com.example.herbverse_customer.ui.screens.wishlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.herbverse_customer.HerbverseApplication
import com.example.herbverse_customer.R
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.ui.screens.product.getImageResourceForProduct
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // In a real app, these would come from a ViewModel
    var wishlistItems by remember { mutableStateOf(getSampleWishlistItems().toMutableList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (wishlistItems.isEmpty() && !isLoading) {
            EmptyWishlist(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Saved Items (${wishlistItems.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(wishlistItems) { product ->
                    WishlistItem(
                        product = product,
                        onProductClick = { onProductClick(product.id) },
                        onRemoveClick = {
                            wishlistItems = wishlistItems.toMutableList().apply { 
                                remove(product) 
                            }
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${product.name} removed from wishlist",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                ).let { result ->
                                    if (result == SnackbarResult.ActionPerformed) {
                                        wishlistItems = wishlistItems.toMutableList().apply {
                                            add(product)
                                        }
                                    }
                                }
                            }
                        },
                        onAddToCartClick = {
                            coroutineScope.launch {
                                // In a real app, add to cart via view model
                                snackbarHostState.showSnackbar("${product.name} added to cart")
                            }
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyWishlist(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your wishlist is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Save items you love to buy them later",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* Navigate to products */ },
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Explore Products")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistItem(
    product: Product,
    onProductClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Image(
                painter = painterResource(id = getImageResourceForProduct(product.id)),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Product info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Action buttons
            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from wishlist",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                IconButton(
                    onClick = onAddToCartClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to cart",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Sample data for demo
private fun getSampleWishlistItems(): List<Product> {
    return listOf(
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
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WishlistScreenPreview() {
    Herbverse_customerTheme {
        WishlistScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyWishlistPreview() {
    Herbverse_customerTheme {
        Surface {
            EmptyWishlist(Modifier.fillMaxSize())
        }
    }
}