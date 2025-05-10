package com.example.herbverse_customer.ui.screens.discovery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.herbverse_customer.R
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerbDiscoveryScreen(
    onNavigateBack: () -> Unit,
    onProductSelected: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedUsage by remember { mutableStateOf<String?>(null) }
    var selectedBenefit by remember { mutableStateOf<String?>(null) }
    
    val leafGreenDark = Color(0xFF33691E)
    
    // Background colors for each step
    val backgroundColor = when (currentStep) {
        0 -> Color(0xFFE8F5E9)
        1 -> Color(0xFFE0F7FA)
        else -> Color(0xFFFFF8E1)
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = when(currentStep) {
                            0 -> "Herb Journey"
                            1 -> "Discover Benefits"
                            else -> "Nature's Remedies"
                        },
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep > 0) {
                            currentStep--
                            if (currentStep == 0) selectedBenefit = null
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = leafGreenDark
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Main content
            when (currentStep) {
                0 -> HerbJourneyScreen(
                    onUsageSelected = { usage ->
                        selectedUsage = usage
                        currentStep = 1
                    }
                )
                1 -> HerbBenefitsScreen(
                    selectedUsage = selectedUsage ?: "General",
                    onBenefitSelected = { benefit ->
                        selectedBenefit = benefit
                        currentStep = 2
                    }
                )
                2 -> NatureRemediesScreen(
                    selectedUsage = selectedUsage ?: "General",
                    selectedBenefit = selectedBenefit ?: "General",
                    onHerbSelected = { herbId ->
                        onProductSelected(herbId)
                    }
                )
            }
        }
    }
}

@Composable
fun HerbJourneyScreen(onUsageSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title section with icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Begin Your Herbal Journey",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF33691E)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "How will you embrace the power of nature?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color(0xFF558B2F)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Usage options in a grid
        val usageOptions = listOf(
            Triple("Culinary", "Enhance your cooking with nature's flavors", R.drawable.category_culinary),
            Triple("Wellness", "Support your body's natural balance", R.drawable.category_medicinal),
            Triple("Tea Blending", "Create soothing herbal infusions", R.drawable.herb_chamomile),
            Triple("Aromatherapy", "Harness the power of natural scents", R.drawable.category_aromatic),
            Triple("Gardening", "Grow your own healing garden", R.drawable.category_dried)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(usageOptions) { (title, description, imageRes) ->
                HerbCard(
                    title = title,
                    description = description,
                    imageRes = imageRes,
                    onClick = { onUsageSelected(title) }
                )
            }
        }
    }
}

@Composable
fun HerbCard(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HerbBenefitsScreen(
    selectedUsage: String,
    onBenefitSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with selected usage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            // Show different images based on the selected usage
            Image(
                painter = painterResource(
                    when (selectedUsage) {
                        "Culinary" -> R.drawable.category_culinary
                        "Wellness" -> R.drawable.category_medicinal
                        "Tea Blending" -> R.drawable.herb_chamomile
                        "Aromatherapy" -> R.drawable.category_aromatic
                        else -> R.drawable.category_dried
                    }
                ),
                contentDescription = selectedUsage,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedUsage,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "What benefits are you seeking?",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF33691E)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Benefits grid based on the selected usage
        val benefitOptions = when(selectedUsage) {
            "Culinary" -> listOf(
                Triple("Italian Kitchen", "Mediterranean herbs for authentic flavors", R.drawable.herb_basil),
                Triple("Spice Blends", "Create personalized herb mixtures", R.drawable.herb_default),
                Triple("Grilling Masters", "Bold flavors for your BBQ", R.drawable.herb_rosemary),
                Triple("Asian Fusion", "Exotic herbs for Eastern cuisine", R.drawable.herb_default),
                Triple("Salad Enhancers", "Fresh herbs to elevate your greens", R.drawable.herb_default)
            )
            "Wellness" -> listOf(
                Triple("Calming", "Herbs that promote relaxation", R.drawable.herb_lavender),
                Triple("Immunity", "Strengthen your body's defenses", R.drawable.herb_echinacea),
                Triple("Digestion", "Support your digestive system", R.drawable.herb_peppermint),
                Triple("Sleep Support", "Natural solutions for better rest", R.drawable.herb_chamomile),
                Triple("Energy & Focus", "Herbs for mental clarity", R.drawable.herb_rosemary)
            )
            "Tea Blending" -> listOf(
                Triple("Relaxation", "Calming blends for stress relief", R.drawable.herb_chamomile),
                Triple("Digestive", "Soothing blends for after meals", R.drawable.herb_peppermint),
                Triple("Bedtime", "Gentle preparations for sleep", R.drawable.herb_lavender),
                Triple("Vitality", "Energizing blends for daytime", R.drawable.herb_default),
                Triple("Seasonal", "Blends for cold & allergy seasons", R.drawable.herb_echinacea)
            )
            "Aromatherapy" -> listOf(
                Triple("Relaxation", "Calming scents for stress relief", R.drawable.herb_lavender),
                Triple("Concentration", "Clarifying aromas for focus", R.drawable.herb_rosemary),
                Triple("Energizing", "Invigorating scents for vitality", R.drawable.herb_peppermint),
                Triple("Sleep", "Soothing fragrances for bedtime", R.drawable.herb_chamomile),
                Triple("Emotional Balance", "Stabilizing scents for mood", R.drawable.herb_default)
            )
            else -> listOf(
                Triple("Beginners", "Easy herbs for new gardeners", R.drawable.herb_basil),
                Triple("Small Spaces", "Perfect for containers & patios", R.drawable.herb_default),
                Triple("Companion Planting", "Herbs that help other plants", R.drawable.herb_default),
                Triple("Pollinators", "Attract bees and butterflies", R.drawable.herb_lavender),
                Triple("Indoor Herb Garden", "Herbs that thrive inside", R.drawable.herb_default)
            )
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(700.dp)
        ) {
            items(benefitOptions) { (title, description, imageRes) ->
                HerbCard(
                    title = title,
                    description = description,
                    imageRes = imageRes,
                    onClick = { onBenefitSelected(title) }
                )
            }
        }
    }
}

@Composable
fun NatureRemediesScreen(
    selectedUsage: String,
    selectedBenefit: String,
    onHerbSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Generate herb recommendations based on selections
    val herbList = remember(selectedUsage, selectedBenefit) {
        when {
            selectedUsage == "Culinary" && selectedBenefit == "Italian Kitchen" -> 
                listOf(
                    HerbDetail("1", "Basil", "The essential herb of Italian cuisine with sweet flavor", R.drawable.herb_basil, 4.8f),
                    HerbDetail("4", "Rosemary", "Aromatic herb with pine-like fragrance", R.drawable.herb_rosemary, 4.7f),
                    HerbDetail("7", "Oregano", "Classic Mediterranean herb with bold flavor", R.drawable.herb_default, 4.6f),
                    HerbDetail("8", "Thyme", "Versatile herb for soups, stews and sauces", R.drawable.herb_default, 4.5f)
                )
            selectedUsage == "Wellness" && selectedBenefit == "Calming" ->
                listOf(
                    HerbDetail("1", "Lavender", "Soothing floral herb with distinctive aroma", R.drawable.herb_lavender, 4.9f),
                    HerbDetail("3", "Chamomile", "Gentle daisy-like herb with apple scent", R.drawable.herb_chamomile, 4.8f),
                    HerbDetail("10", "Lemon Balm", "Lemon-scented member of the mint family", R.drawable.herb_default, 4.6f),
                    HerbDetail("11", "Passionflower", "Beautiful flowering vine with sedative effects", R.drawable.herb_default, 4.5f)
                )
            selectedUsage == "Tea Blending" && selectedBenefit == "Bedtime" ->
                listOf(
                    HerbDetail("3", "Chamomile", "Sweet, apple-like aroma perfect for nighttime", R.drawable.herb_chamomile, 4.9f),
                    HerbDetail("1", "Lavender", "Soothing floral notes for deep relaxation", R.drawable.herb_lavender, 4.7f),
                    HerbDetail("12", "Valerian Root", "Earthy herb with powerful sleep benefits", R.drawable.herb_default, 4.5f),
                    HerbDetail("10", "Lemon Balm", "Gentle citrus notes that promote relaxation", R.drawable.herb_default, 4.6f)
                )
            // Add more combinations as needed
            else -> listOf(
                HerbDetail("2", "Basil", "Versatile culinary herb with sweet aroma", R.drawable.herb_basil, 4.8f),
                HerbDetail("5", "Peppermint", "Cooling herb with diverse applications", R.drawable.herb_peppermint, 4.7f),
                HerbDetail("4", "Rosemary", "Aromatic herb for cooking and wellness", R.drawable.herb_rosemary, 4.6f),
                HerbDetail("1", "Lavender", "Calming herb with beautiful purple flowers", R.drawable.herb_lavender, 4.5f)
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Section header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Nature's Pharmacy",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "For $selectedBenefit",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = selectedUsage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Image(
                        painter = painterResource(
                            when (selectedUsage) {
                                "Culinary" -> R.drawable.herb_basil
                                "Wellness" -> R.drawable.herb_lavender
                                "Tea Blending" -> R.drawable.herb_chamomile
                                "Aromatherapy" -> R.drawable.herb_rosemary
                                else -> R.drawable.herb_default
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // List of recommended herbs
        herbList.forEach { herb ->
            RecommendedHerbCard(herb = herb, onClick = { onHerbSelected(herb.id) })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class HerbDetail(
    val id: String,
    val name: String,
    val description: String,
    val imageRes: Int,
    val rating: Float
)

@Composable
fun RecommendedHerbCard(herb: HerbDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = herb.imageRes),
                    contentDescription = herb.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = herb.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < herb.rating) 
                                        Color(0xFFFFC107) 
                                    else 
                                        Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = herb.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Explore")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HerbDiscoveryScreenPreview() {
    Herbverse_customerTheme {
        HerbDiscoveryScreen(
            onNavigateBack = {},
            onProductSelected = {}
        )
    }
}