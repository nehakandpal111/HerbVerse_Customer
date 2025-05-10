package com.example.herbverse_customer.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.herbverse_customer.R
import com.example.herbverse_customer.data.model.Product
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Product>>(emptyList()) }
    
    // Sample products to search from (in a real app, this would come from a ViewModel)
    val allProducts = remember { sampleProducts + globalHerbs }
    
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            isSearching = true
            // Simulate network delay
            delay(500)
            searchResults = allProducts.filter { 
                it.name.contains(searchText, ignoreCase = true) || 
                it.shortDescription.contains(searchText, ignoreCase = true) 
            }
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Herbs") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search box with background image
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.banner_fresh_herbs),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Semi-transparent overlay for better text visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
                
                // Search text and field
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        "Find Your Perfect Herb",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Medicinal, culinary, or aromatic herbs from around the world",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search by name or description...", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            }
            
            // Search results or initial content
            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (searchText.isNotEmpty()) {
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No herbs found matching '$searchText'",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${searchResults.size} herbs found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(searchResults) { product ->
                            SearchResultItem(product = product, onClick = { onProductClick(product.id) })
                        }
                    }
                }
            } else {
                // Initial content when no search is performed
                PopularSearchCategories(onCategoryClick = { category ->
                    searchText = category
                })
            }
        }
    }
}

@Composable
fun SearchResultItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
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
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = product.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val categoryName = when(product.categoryId) {
                        "1" -> "Medicinal"
                        "2" -> "Culinary"
                        "3" -> "Aromatic"
                        else -> "Other"
                    }
                    
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PopularSearchCategories(onCategoryClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Popular Search Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Popular categories
        SearchCategoryItem("Medicinal Herbs", onCategoryClick)
        SearchCategoryItem("Culinary Herbs", onCategoryClick)
        SearchCategoryItem("Aromatic Herbs", onCategoryClick)
        SearchCategoryItem("Asian Herbs", onCategoryClick)
        SearchCategoryItem("Mediterranean Herbs", onCategoryClick)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Trending Searches",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trending searches
        SearchCategoryItem("Lavender", onCategoryClick)
        SearchCategoryItem("Turmeric", onCategoryClick)
        SearchCategoryItem("Holy Basil", onCategoryClick)
        SearchCategoryItem("Ginseng", onCategoryClick)
    }
}

@Composable
fun SearchCategoryItem(category: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(category) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = category,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Sample data
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