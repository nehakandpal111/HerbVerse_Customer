package com.example.herbverse_customer.ui.screens.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import com.example.herbverse_customer.HerbverseApplication
import com.example.herbverse_customer.R
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme
import com.example.herbverse_customer.ui.viewmodels.CartViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String = "1",
    onBackClick: () -> Unit = {},
    onAddToCart: (Product, Int) -> Unit = { _, _ -> },
    onCheckout: () -> Unit = {}
) {
    // In a real app, you would fetch product details from a repository or ViewModel
    val product = sampleProducts.find { it.id == productId } ?: sampleProducts.first()
    var quantity by remember { mutableIntStateOf(1) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Description", "Reviews", "Vendor")
    var isAddedToCart by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val cartViewModel = remember { (context.applicationContext as HerbverseApplication).cartViewModel }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onCheckout) {
                        Icon(
                            Icons.Filled.ShoppingCart, 
                            contentDescription = "Go to cart",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("DISMISS")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // Product image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Image(
                        painter = painterResource(id = getImageResourceForProduct(product.id)),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                // Price section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${product.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = product.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = product.fullDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item {
                // Quantity selector
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        TextField(
                            value = quantity.toString(),
                            onValueChange = { 
                                val newValue = it.toIntOrNull() ?: 1
                                if (newValue > 0) quantity = newValue
                            },
                            modifier = Modifier.width(64.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Button(
                            onClick = { 
                                // Auto-select this option
                                isAddedToCart = true
                            },
                            modifier = Modifier
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Auto", fontSize = 12.sp)
                            if (isAddedToCart) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Add to cart button
                Button(
                    onClick = { 
                        coroutineScope.launch {
                            try {
                                // Use cart view model to add to cart
                                cartViewModel.addToCart(product.id, quantity)
                                snackbarMessage = "${product.name} added to cart"
                                showSnackbar = true
                                // Also call the passed in onAddToCart for navigation if needed
                                onAddToCart(product, quantity)
                            } catch (e: Exception) {
                                snackbarMessage = "Error: ${e.message}"
                                showSnackbar = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart, 
                            contentDescription = "Add to cart",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Cart",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
            
            item {
                // Checkout button
                FilledTonalButton(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            item {
                // Tabs for more details
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                
                // Tab content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> ProductDescription(product)
                        1 -> ProductReviews()
                        2 -> ProductVendorInfo()
                    }
                }
            }
        }
    }
}

// Function to get the appropriate image resource based on product ID
fun getImageResourceForProduct(productId: String): Int {
    return when (productId) {
        "1" -> R.drawable.herb_lavender
        "2" -> R.drawable.herb_basil
        "3" -> R.drawable.herb_chamomile
        "4" -> R.drawable.herb_rosemary
        "5" -> R.drawable.herb_peppermint
        "6" -> R.drawable.herb_echinacea
        else -> R.drawable.herb_default
    }
}

@Composable
fun ProductDescription(product: Product) {
    Column {
        Text(
            text = "About this herb",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = product.fullDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Product benefits
        Text(
            text = "Benefits",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        BenefitRow("Aromatherapy", "Great for relaxation and reducing stress")
        BenefitRow("Culinary", "Adds flavor to dishes and drinks")
        BenefitRow("Medicinal", "May help with mild anxiety and sleep issues")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // How to use
        Text(
            text = "How to Use",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Steep 1-2 teaspoons in hot water for 5 minutes for a soothing tea. Use in bath water for aromatherapy benefits. Add to soups, stews, and sauces for culinary purposes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun BenefitRow(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ProductReviews() {
    Column {
        Text(
            text = "Customer Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Overall rating
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "4.5/5",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "based on 18 reviews",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                RatingBar(rating = 5, count = 10)
                RatingBar(rating = 4, count = 5)
                RatingBar(rating = 3, count = 2)
                RatingBar(rating = 2, count = 1)
                RatingBar(rating = 1, count = 0)
            }
        }
        
        HorizontalDivider()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Write review button
        OutlinedButton(
            onClick = { /* Navigate to write review page */ },
            modifier = Modifier.fillMaxWidth(),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Text("Write a Review")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Individual reviews
        sampleDetailReviews.forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RatingBar(rating: Int, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (index < rating) MaterialTheme.colorScheme.tertiary else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ReviewItem(review: DetailReview) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.userName.first().toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < review.rating) MaterialTheme.colorScheme.tertiary else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = review.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = review.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium
        )
        
        if (review.response != null) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Response from Vendor:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = review.response,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProductVendorInfo() {
    Column {
        Text(
            text = "About the Vendor",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Vendor logo/image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "WC",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Willow Creek Farms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = "4.8",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Text(
                        text = "(120 reviews)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                HorizontalDivider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Family-owned, organic herb farm for over 30 years. We specialize in growing the highest quality medicinal and culinary herbs using sustainable farming practices.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Our Story",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Willow Creek Farms began in 1987 when Maria and John Williams purchased a small plot of land in the Pacific Northwest. What started as a passion project growing herbs for their own kitchen soon blossomed into a thriving business.\n\nToday, the second generation of the Williams family continues their legacy, maintaining their commitment to organic growing practices and sustainable agriculture. They work with local communities to promote environmental stewardship and provide education about the benefits of herbs.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Sustainability Practices",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column {
            SustainabilityPracticeItem(
                title = "Organic Certified",
                description = "All our herbs are grown without synthetic pesticides or fertilizers"
            )
            
            SustainabilityPracticeItem(
                title = "Water Conservation",
                description = "Drip irrigation systems reduce water usage by 60%"
            )
            
            SustainabilityPracticeItem(
                title = "Solar Powered",
                description = "Our drying facility runs on 100% renewable solar energy"
            )
            
            SustainabilityPracticeItem(
                title = "Biodiversity",
                description = "We maintain 30% of our land as natural habitat for pollinators"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { /* Navigate to vendor page */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Visit Vendor Page")
        }
    }
}

@Composable
fun SustainabilityPracticeItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // This would typically be an icon
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// Sample data for product details
private val sampleProducts = listOf(
    Product(
        id = "1",
        name = "Lavender",
        shortDescription = "Fresh aromatic herb with soothing properties",
        fullDescription = "Lavender is well known for its fragrance and medicinal properties. It promotes relaxation and can help with sleep, anxiety, and more. Our lavender is organically grown without pesticides or artificial fertilizers, ensuring you get the purest product possible. The flowers are carefully harvested at peak bloom for maximum potency and aroma.",
        price = 9.99,
        stock = 50,
        categoryId = "3",
        imageUrl = ""
    ),
    Product(
        id = "2",
        name = "Basil",
        shortDescription = "Fresh culinary herb with a sweet aroma",
        fullDescription = "Basil is a culinary herb of the family Lamiaceae. It is a tender plant, used in cuisines worldwide. Our basil is grown in nutrient-rich soil and harvested at the perfect moment to ensure maximum flavor. It's perfect for adding to pasta, salads, and many other dishes.",
        price = 4.99,
        stock = 100,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "3",
        name = "Chamomile",
        shortDescription = "Herbal remedy for relaxation and sleep",
        fullDescription = "Chamomile is known for its soothing properties and commonly used in teas to help with sleep and digestive issues.",
        price = 7.99,
        stock = 80,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "4",
        name = "Rosemary",
        shortDescription = "Fragrant herb with needle-like leaves",
        fullDescription = "Rosemary is a fragrant evergreen herb with needle-like leaves. It's used as a culinary condiment, to make bodily perfumes, and for its health benefits.",
        price = 5.99,
        stock = 70,
        categoryId = "2",
        imageUrl = ""
    ),
    Product(
        id = "5",
        name = "Peppermint",
        shortDescription = "Cooling herb perfect for teas and remedies",
        fullDescription = "Peppermint is a hybrid mint, a cross between watermint and spearmint. The plant is widely used in teas and as a flavoring agent.",
        price = 6.99,
        stock = 90,
        categoryId = "1",
        imageUrl = ""
    ),
    Product(
        id = "6",
        name = "Echinacea",
        shortDescription = "Fresh herb with medicinal properties",
        fullDescription = "Echinacea is a flowering plant that is native to North America. It is commonly used to boost the immune system and fight off infections. Our echinacea is grown using sustainable farming practices and is carefully harvested to ensure maximum potency.",
        price = 7.99,
        stock = 75,
        categoryId = "1",
        imageUrl = ""
    )
)

// Sample data for detailed reviews
data class DetailReview(
    val userName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val response: String? = null
)

private val sampleDetailReviews = listOf(
    DetailReview(
        userName = "Sarah J.",
        rating = 5,
        title = "Amazing quality!",
        comment = "The lavender is amazing! It smells heavenly and I've been using it for my homemade bath salts. Will definitely purchase again! The packaging was also eco-friendly which I really appreciated.",
        date = "May 12, 2023",
        response = "Thank you for your kind review, Sarah! We're so glad you're enjoying our lavender. We put a lot of care into our packaging to ensure it's sustainable."
    ),
    DetailReview(
        userName = "Michael T.",
        rating = 4,
        title = "Good product, slow shipping",
        comment = "Fresh lavender that lasted much longer than store-bought. Great aroma and quality. The only downside was the shipping took longer than expected.",
        date = "April 3, 2023"
    ),
    DetailReview(
        userName = "Emma R.",
        rating = 5,
        title = "Perfect for my needs",
        comment = "This lavender is perfect for my evening tea ritual. It helps me relax and sleep better. Wonderful quality!",
        date = "March 22, 2023"
    )
)

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    Herbverse_customerTheme {
        Surface {
            ProductDetailScreen()
        }
    }
}