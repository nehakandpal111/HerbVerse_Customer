package com.example.herbverse_customer.ui.screens.discovery

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.herbverse_customer.R
import com.example.herbverse_customer.navigation.LocalNavController
import com.example.herbverse_customer.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerbWorldMapScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("World Map", "By Continent", "By Climate")
    var selectedHerb by remember { mutableStateOf<HerbLocation?>(null) }
    var selectedHerbType by remember { mutableStateOf<HerbType?>(null) }
    var showIntroDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Discover Herbs Worldwide") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share map */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* Open search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
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
            // Tab selector for different map views
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Herb type filter chips
            HerbTypeFilters(
                selectedType = selectedHerbType,
                onTypeSelected = { selectedHerbType = it }
            )
            
            when (selectedTabIndex) {
                0 -> WorldMapView(
                    onHerbSelected = { selectedHerb = it },
                    selectedType = selectedHerbType
                )
                1 -> ContinentView()
                2 -> ClimateView()
            }
        }
        
        // Show herb detail dialog when a pin is tapped
        selectedHerb?.let { herb ->
            HerbDetailDialog(
                herb = herb,
                onDismiss = { selectedHerb = null }
            )
        }
        
        // First-time intro dialog
        if (showIntroDialog) {
            IntroDialog(onDismiss = { showIntroDialog = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerbTypeFilters(
    selectedType: HerbType?,
    onTypeSelected: (HerbType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onTypeSelected(null) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedType == null) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "All Herbs",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { 
                    onTypeSelected(if (selectedType == HerbType.MEDICINAL) null else HerbType.MEDICINAL) 
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedType == HerbType.MEDICINAL) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(HerbType.MEDICINAL.color, CircleShape)
                )
                Text(
                    "Medicinal",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { 
                    onTypeSelected(if (selectedType == HerbType.CULINARY) null else HerbType.CULINARY) 
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedType == HerbType.CULINARY) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(HerbType.CULINARY.color, CircleShape)
                )
                Text(
                    "Culinary",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun WorldMapView(
    onHerbSelected: (HerbLocation) -> Unit,
    selectedType: HerbType?
) {
    val herbs = remember { herbLocations }.let { herbs ->
        selectedType?.let { type -> herbs.filter { it.type == type } } ?: herbs
    }
    
    // Map zoom and pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    
    // Add state for tracking when to show vendors
    var showVendors by remember { mutableStateOf(false) }
    var selectedVendor by remember { mutableStateOf<VendorLocation?>(null) }
    
    // Update showVendors based on zoom level
    LaunchedEffect(scale) {
        showVendors = scale > 1.5f
    }
    
    // Get the container size to calculate proper positioning
    var mapContainerSize by remember { mutableStateOf(Size.Zero) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F8FC))
    ) {
        // World map with interactive zoom/pan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Smoothly adjust scale with better limits
                        scale = (scale * zoom).coerceIn(0.5f, 2.5f)
                        // Limit panning based on scale
                        val maxOffsetX = (mapContainerSize.width * (scale - 1f) / 2f).coerceAtLeast(0f)
                        val maxOffsetY = (mapContainerSize.height * (scale - 1f) / 2f).coerceAtLeast(0f)
                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                            y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    }
                }
                .onGloballyPositioned { coordinates ->
                    mapContainerSize = Size(
                        width = coordinates.size.width.toFloat(),
                        height = coordinates.size.height.toFloat()
                    )
                }
        ) {
            // World map background with high quality image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // Use the real world map image instead of drawing shapes
                Image(
                    painter = painterResource(id = R.drawable.world_map), // Use the PNG image
                    contentDescription = "World Map",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
                
                if (mapContainerSize != Size.Zero) {
                    // Draw herb pins on the map itself so they stay fixed relative to the map
                    herbs.forEach { herb ->
                        HerbPinFixed(
                            herb = herb,
                            onClick = { onHerbSelected(herb) }
                        )
                    }
                    
                    // Show vendors when zoomed in sufficiently
                    if (showVendors) {
                        vendorLocations.forEach { vendor ->
                            // Only show vendors that deal with the selected herb type (or all if no filter)
                            if (selectedType == null || vendor.herbTypes.contains(selectedType)) {
                                VendorPinFixed(
                                    vendor = vendor,
                                    onClick = { selectedVendor = vendor }
                                )
                            }
                        }
                    }
                }
            }

            // Draw continent labels with nice styling
            ContinentLabels(scale, offset)
        }
        
        // Zoom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { 
                    // Smoother zoom in with constraints
                    scale = (scale * 1.2f).coerceIn(0.5f, 2.5f)
                },
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom in",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            FloatingActionButton(
                onClick = { 
                    // Smoother zoom out with constraints
                    scale = (scale / 1.2f).coerceIn(0.5f, 2.5f)
                },
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom out",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Reset button
            FloatingActionButton(
                onClick = { 
                    // Reset position and zoom
                    scale = 1f
                    offset = Offset.Zero
                },
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset view",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Legend
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    "Herb Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                legendItem(
                    color = HerbType.MEDICINAL.color,
                    text = "Medicinal",
                    count = herbLocations.count { it.type == HerbType.MEDICINAL }
                )
                
                legendItem(
                    color = HerbType.CULINARY.color,
                    text = "Culinary",
                    count = herbLocations.count { it.type == HerbType.CULINARY }
                )
                
                legendItem(
                    color = HerbType.AROMATIC.color,
                    text = "Aromatic",
                    count = herbLocations.count { it.type == HerbType.AROMATIC }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    "Total: ${herbLocations.size} herbs",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
                
                if (scale > 1.5f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Vendor Locations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        "${vendorLocations.size} vendors visible",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Instructions
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                    
                    Text(
                        "Tap on herb pins to see details",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (scale > 1.4f) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Vendor locations are now visible",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Zoom in to see vendor locations",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        
        // Vendor detail dialog
        selectedVendor?.let { vendor ->
            VendorDetailDialog(
                vendor = vendor,
                onDismiss = { selectedVendor = null }
            )
        }
    }
}

@Composable
fun VendorPinFixed(
    vendor: VendorLocation,
    onClick: () -> Unit
) {
    // Position the pin based on the normalized coordinates
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        // Calculate absolute position from normalized coordinates
        Box(
            modifier = Modifier
                .offset(
                    // Use the entire map area by multiplying by large values
                    x = (vendor.x * 3000).dp,
                    y = (vendor.y * 2000).dp
                )
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF3F51B5).copy(alpha = 0.9f))
                    .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                    .shadow(2.dp, RoundedCornerShape(4.dp))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = vendor.name,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun VendorPin(
    vendor: VendorLocation,
    scale: Float,
    offset: Offset,
    onClick: () -> Unit
) {
    // Old implementation, keeping for reference
    // Calculate position based on normalized coordinates, scale and offset
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        // Only render pins that are potentially visible
        val pinX = (vendor.x * 350 + offset.x) * scale
        val pinY = (vendor.y * 350 + offset.y) * scale
        
        // Skip rendering pins that are definitely outside the visible area
        if (pinX < -100 || pinX > 500 || pinY < -100 || pinY > 500) {
            return@Box
        }
        
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = pinX.roundToInt(),
                        y = pinY.roundToInt()
                    )
                }
                .size(42.dp * scale.coerceIn(0.5f, 1.2f))
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF3F51B5).copy(alpha = 0.9f))
                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = vendor.name,
                tint = Color.White,
                modifier = Modifier.size(20.dp * scale.coerceIn(0.5f, 1.2f))
            )
        }
    }
}

@Composable
fun VendorDetailDialog(
    vendor: VendorLocation,
    onDismiss: () -> Unit
) {
    val navController = LocalNavController.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with name and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = vendor.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                
                // Vendor image
                Image(
                    painter = painterResource(vendor.imageRes),
                    contentDescription = vendor.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Location
                Text(
                    text = "Location: ${vendor.location}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Specialty
                Text(
                    text = "Specialty: ${vendor.specialty}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating: ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < vendor.rating) Color(0xFFFFD700) else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = " (${vendor.rating})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Herb types
                Text(
                    text = "Herb categories: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vendor.herbTypes.forEach { herbType ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(herbType.label) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = herbType.color.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // View button
                Button(
                    onClick = { 
                        onDismiss()
                        // Navigate to vendor detail screen
                        navController.navigate("vendorDetail/${vendor.id}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Vendor Profile")
                }
            }
        }
    }
}

@Composable
private fun legendItem(color: Color, text: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
                .border(0.5.dp, Color.White, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContinentLabels(scale: Float, offset: Offset) {
    val continentLabels = remember {
        mapOf(
            "North America" to Pair(0.2f, 0.25f),
            "South America" to Pair(0.25f, 0.55f),
            "Europe" to Pair(0.45f, 0.25f),
            "Africa" to Pair(0.45f, 0.45f),
            "Asia" to Pair(0.65f, 0.3f),
            "Australia" to Pair(0.75f, 0.6f),
            "Antarctica" to Pair(0.5f, 0.85f)
        )
    }
    
    continentLabels.forEach { (name, position) ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = ((position.first * 350 + offset.x) * scale).roundToInt(),
                            y = ((position.second * 350 + offset.y) * scale).roundToInt()
                        )
                    }
            )
        }
    }
}

@Composable
fun HerbPinFixed(
    herb: HerbLocation,
    onClick: () -> Unit
) {
    // Position the pin based on the normalized coordinates
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        // Calculate absolute position from normalized coordinates
        // Use a wider scale to better distribute pins
        Box(
            modifier = Modifier
                .offset(
                    // Use the entire map area by multiplying by large values
                    x = (herb.x * 3000).dp,
                    y = (herb.y * 2000).dp
                )
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(herb.type.color.copy(alpha = 0.8f))
                    .border(1.dp, Color.White, CircleShape)
                    .shadow(2.dp, CircleShape)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = herb.name,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun HerbPin(
    herb: HerbLocation,
    scale: Float,
    offset: Offset,
    onClick: () -> Unit
) {
    // This is the old implementation - we'll keep it for reference but not use it
    var isPulsing by remember { mutableStateOf(true) }
    
    // Optimize animation by using a lighter animation
    val pulseFactor by animateFloatAsState(
        targetValue = if (isPulsing) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Use LaunchedEffect correctly
    LaunchedEffect(Unit) {
        isPulsing = true
    }
    
    // Calculate position based on normalized coordinates, scale and offset
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        // Only render pins that are potentially visible to improve performance
        val pinX = (herb.x * 350 + offset.x) * scale
        val pinY = (herb.y * 350 + offset.y) * scale
        
        // Skip rendering pins that are definitely outside the visible area
        if (pinX < -100 || pinX > 500 || pinY < -100 || pinY > 500) {
            return@Box
        }
        
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = pinX.roundToInt(),
                        y = pinY.roundToInt()
                    )
                }
                .size(48.dp * scale.coerceIn(0.5f, 1.2f))
                .padding(6.dp)
                .clip(CircleShape)
                .background(herb.type.color.copy(alpha = 0.7f))
                .border(2.dp, Color.White, CircleShape) // Add white border for visibility
                .shadow(4.dp, CircleShape) // Add shadow for depth
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // Simplified pin style for better visibility
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = herb.name,
                tint = Color.White, // White icon for better contrast
                modifier = Modifier
                    .size(24.dp * scale.coerceIn(0.5f, 1.2f))
                    .graphicsLayer(
                        scaleX = if (isPulsing) pulseFactor else 1f,
                        scaleY = if (isPulsing) pulseFactor else 1f
                    )
            )
        }
    }
}

@Composable
fun IntroDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Discover Herbs From Around The World",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Explore our interactive map to discover medicinal, culinary, and aromatic herbs from different regions.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• Tap on herb pins to learn more about each herb\n• Filter herbs by type using the filter chips\n• Zoom and pan to navigate the map",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Exploring")
                }
            }
        }
    }
}

@Composable
fun ContinentView() {
    val continents = remember {
        listOf("Asia", "Africa", "North America", "South America", "Europe", "Australia")
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(continents) { continent ->
            ContinentCard(continent = continent)
        }
    }
}

@Composable
fun ContinentCard(continent: String) {
    val (backgroundImage, herbCount) = when(continent) {
        "Asia" -> Pair(R.drawable.category_aromatic, 45)
        "Africa" -> Pair(R.drawable.category_medicinal, 32)
        "North America" -> Pair(R.drawable.herb_echinacea, 28)
        "South America" -> Pair(R.drawable.category_culinary, 24)
        "Europe" -> Pair(R.drawable.herb_lavender, 36)
        "Australia" -> Pair(R.drawable.herb_rosemary, 15)
        else -> Pair(R.drawable.herb_default, 0)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { /* Navigate to continent herbs */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Semi-transparent overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            
            // Continent info
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = continent,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$herbCount herbs available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    
                    Button(
                        onClick = { /* Navigate to continent herbs */ },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Explore")
                    }
                }
            }
        }
    }
}

@Composable
fun ClimateView() {
    val climates = remember {
        listOf(
            "Tropical", 
            "Mediterranean", 
            "Desert", 
            "Temperate", 
            "Alpine", 
            "Coastal"
        )
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(climates) { climate ->
            ClimateCard(climate = climate)
        }
    }
}

@Composable
fun ClimateCard(climate: String) {
    val (backgroundImage, description) = when(climate) {
        "Tropical" -> Pair(
            R.drawable.category_culinary, 
            "Hot and humid regions with herbs like Turmeric, Ginger, and Lemongrass"
        )
        "Mediterranean" -> Pair(
            R.drawable.herb_rosemary,
            "Mild winters and dry summers with herbs like Rosemary, Thyme, and Oregano"
        )
        "Desert" -> Pair(
            R.drawable.category_dried,
            "Arid regions with drought-resistant herbs like Sage, Ephedra, and Desert Lavender"
        )
        "Temperate" -> Pair(
            R.drawable.herb_chamomile,
            "Moderate temperature regions with herbs like Chamomile, Peppermint, and Echinacea"
        )
        "Alpine" -> Pair(
            R.drawable.category_aromatic,
            "High altitude regions with herbs like Arnica, Gentian, and Edelweiss"
        )
        "Coastal" -> Pair(
            R.drawable.category_medicinal,
            "Seashore environments with herbs like Sea Fennel, Samphire, and Sea Holly"
        )
        else -> Pair(R.drawable.herb_default, "")
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { /* Navigate to climate herbs */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Semi-transparent overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            
            // Climate info
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = climate,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Button(
                    onClick = { /* Navigate to climate herbs */ },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Herbs")
                }
            }
        }
    }
}

@Composable
fun HerbDetailDialog(
    herb: HerbLocation,
    onDismiss: () -> Unit
) {
    val navController = LocalNavController.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with name and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = herb.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                
                // Herb image
                Image(
                    painter = painterResource(herb.imageRes),
                    contentDescription = herb.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Origin
                Text(
                    text = "Origin: ${herb.origin}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = herb.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Type badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(herb.type.color, CircleShape)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = herb.type.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Vendors count
                Text(
                    text = "${herb.vendorsCount} vendors selling this herb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // View button
                Button(
                    onClick = { 
                        onDismiss()
                        navController.navigate("product_detail/${herb.productId}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Product Details")
                }
            }
        }
    }
}

// Herb types with colors
enum class HerbType(val label: String, val color: Color) {
    MEDICINAL("Medicinal", Color(0xFF4CAF50)),
    CULINARY("Culinary", Color(0xFF2196F3)),
    AROMATIC("Aromatic", Color(0xFFFF9800))
}

// Herb location data class
data class HerbLocation(
    val name: String,
    val x: Float, // Normalized x position (0.0 to 1.0)
    val y: Float, // Normalized y position (0.0 to 1.0)
    val origin: String,
    val description: String,
    val type: HerbType,
    val imageRes: Int,
    val vendorsCount: Int,
    val productId: String
)

// Sample herb locations across the world
val herbLocations = listOf(
    HerbLocation(
        name = "Turmeric",
        x = 0.71f, 
        y = 0.42f,
        origin = "India",
        description = "A powerful medicinal herb with anti-inflammatory properties, used in cooking and traditional Ayurvedic medicine for centuries.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 24,
        productId = "7"
    ),
    HerbLocation(
        name = "Lavender",
        x = 0.48f, 
        y = 0.33f,
        origin = "Mediterranean",
        description = "Known for its distinct floral aroma and calming properties, widely used in aromatherapy, cosmetics, and culinary applications.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_lavender,
        vendorsCount = 32,
        productId = "1"
    ),
    HerbLocation(
        name = "Basil",
        x = 0.49f, 
        y = 0.34f,
        origin = "Italy",
        description = "A fragrant culinary herb essential in Italian cuisine, particularly in pesto sauces and with tomato dishes.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_basil,
        vendorsCount = 28,
        productId = "2"
    ),
    HerbLocation(
        name = "Echinacea",
        x = 0.23f, 
        y = 0.32f,
        origin = "North America",
        description = "Native to North America and used by indigenous peoples for medicinal purposes, now popular for immune support.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_echinacea,
        vendorsCount = 15,
        productId = "6"
    ),
    HerbLocation(
        name = "Lemongrass",
        x = 0.77f, 
        y = 0.43f,
        origin = "Southeast Asia",
        description = "Citrusy herb widely used in Asian cuisine, particularly Thai and Vietnamese, with a bright lemon flavor.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 18,
        productId = "10"
    ),
    HerbLocation(
        name = "Chamomile",
        x = 0.46f, 
        y = 0.29f,
        origin = "Europe",
        description = "Daisy-like flowers used in herbal teas known for promoting relaxation and aiding sleep.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_chamomile,
        vendorsCount = 22,
        productId = "3"
    ),
    HerbLocation(
        name = "Sage",
        x = 0.46f, 
        y = 0.33f,
        origin = "Mediterranean",
        description = "Aromatic herb with earthy flavor, used for centuries in cooking and traditional medicine.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 19,
        productId = "11"
    ),
    HerbLocation(
        name = "Peppermint",
        x = 0.47f, 
        y = 0.27f,
        origin = "Europe",
        description = "Refreshing herb with cooling properties, used in teas, desserts, and for digestive health.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_peppermint,
        vendorsCount = 27,
        productId = "5"
    ),
    HerbLocation(
        name = "Holy Basil (Tulsi)",
        x = 0.72f, 
        y = 0.39f,
        origin = "India",
        description = "Sacred herb in Hindu tradition with adaptogenic properties, used in teas and Ayurvedic medicine.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 16,
        productId = "12"
    ),
    HerbLocation(
        name = "Rosemary",
        x = 0.45f, 
        y = 0.35f,
        origin = "Mediterranean",
        description = "Fragrant herb with needle-like leaves, used in cooking, particularly with roasted meats and potatoes.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_rosemary,
        vendorsCount = 30,
        productId = "4"
    ),
    HerbLocation(
        name = "Ginseng",
        x = 0.81f, 
        y = 0.29f,
        origin = "East Asia",
        description = "Slow-growing root herb prized in traditional Chinese medicine for its energy-boosting and adaptogenic properties.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 12,
        productId = "8"
    ),
    // Additional herbs from around the world
    HerbLocation(
        name = "Thyme",
        x = 0.47f, 
        y = 0.34f,
        origin = "Mediterranean",
        description = "Versatile culinary herb with tiny aromatic leaves, used in many European cuisines and has antiseptic properties.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 25,
        productId = "13"
    ),
    HerbLocation(
        name = "Cinnamon",
        x = 0.73f, 
        y = 0.46f,
        origin = "Sri Lanka",
        description = "Aromatic spice derived from tree bark, used in both sweet and savory dishes and has anti-inflammatory properties.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 31,
        productId = "14"
    ),
    HerbLocation(
        name = "Ginger",
        x = 0.74f, 
        y = 0.42f,
        origin = "Southeast Asia",
        description = "Pungent rhizome used in cooking and traditional medicine for digestive health and reducing inflammation.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 29,
        productId = "15"
    ),
    HerbLocation(
        name = "Cilantro",
        x = 0.21f, 
        y = 0.38f,
        origin = "Mexico",
        description = "Herb with distinctive flavor used extensively in Mexican, Indian, and Southeast Asian cuisines.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 22,
        productId = "16"
    ),
    HerbLocation(
        name = "Eucalyptus",
        x = 0.85f, 
        y = 0.65f,
        origin = "Australia",
        description = "Aromatic leaves used for oil extraction, known for respiratory benefits and distinctive scent.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "17"
    ),
    HerbLocation(
        name = "Saffron",
        x = 0.56f, 
        y = 0.38f,
        origin = "Iran",
        description = "World's most expensive spice derived from flower stigmas, used for its distinctive color, flavor, and medicinal properties.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "18"
    ),
    HerbLocation(
        name = "Valerian",
        x = 0.48f, 
        y = 0.26f,
        origin = "Europe & Asia",
        description = "Perennial flowering plant used for its sedative effects and sleep promotion since ancient times.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "19"
    ),
    HerbLocation(
        name = "Yerba Mate",
        x = 0.30f, 
        y = 0.62f,
        origin = "South America",
        description = "Traditional South American caffeine-rich herbal tea known for providing energy and focus.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 17,
        productId = "20"
    ),
    HerbLocation(
        name = "Kaffir Lime",
        x = 0.76f, 
        y = 0.42f,
        origin = "Thailand",
        description = "Citrus fruit whose leaves are essential in Thai cooking for their distinctive aroma and flavor.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 13,
        productId = "21"
    ),
    HerbLocation(
        name = "Rooibos",
        x = 0.52f, 
        y = 0.62f,
        origin = "South Africa",
        description = "Red herbal tea from South Africa with high antioxidants and no caffeine, known for its smooth taste.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "22"
    ),
    // Adding more region-specific herbs
    HerbLocation(
        name = "Ashwagandha",
        x = 0.68f, 
        y = 0.41f,
        origin = "India",
        description = "Ancient medicinal herb used in Ayurvedic medicine to reduce stress and anxiety and improve concentration.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 18,
        productId = "23"
    ),
    HerbLocation(
        name = "Dill",
        x = 0.51f, 
        y = 0.28f,
        origin = "Eastern Europe",
        description = "Feathery herb with fresh flavor used in pickling, fish dishes, and Eastern European cuisine.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 21,
        productId = "24"
    ),
    HerbLocation(
        name = "Kava",
        x = 0.93f, 
        y = 0.55f,
        origin = "Pacific Islands",
        description = "South Pacific herb used ceremonially for its relaxing and stress-reducing properties.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "25"
    ),
    HerbLocation(
        name = "Za'atar",
        x = 0.57f, 
        y = 0.37f,
        origin = "Middle East",
        description = "Middle Eastern herb blend typically containing thyme, sumac, and sesame seeds, used in many dishes.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 16,
        productId = "26"
    ),
    HerbLocation(
        name = "Epazote",
        x = 0.19f, 
        y = 0.40f,
        origin = "Mexico",
        description = "Strong-flavored herb used in Mexican cuisine, particularly with beans to reduce gas and bloating.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "27"
    ),
    HerbLocation(
        name = "Rhodiola",
        x = 0.58f, 
        y = 0.23f,
        origin = "Arctic regions",
        description = "Cold-climate adaptogen used traditionally in Russia and Scandinavia for physical endurance and stress resistance.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "28"
    ),
    HerbLocation(
        name = "Lemon Verbena",
        x = 0.43f, 
        y = 0.35f,
        origin = "South America",
        description = "Intensely lemony herb used in teas, desserts and aromatherapy, originally from Argentina and Chile.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "29"
    ),
    HerbLocation(
        name = "Galangal",
        x = 0.77f, 
        y = 0.44f,
        origin = "Southeast Asia",
        description = "Rhizome similar to ginger but with citrusy, piney flavor, essential in Thai and Indonesian cuisine.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 13,
        productId = "30"
    ),
    // Adding more herbs from India and Himalayas
    HerbLocation(
        name = "Brahmi",
        x = 0.70f, 
        y = 0.40f,
        origin = "India",
        description = "Memory-enhancing herb used in traditional Ayurvedic medicine, known for improving cognitive function and reducing anxiety.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "31"
    ),
    HerbLocation(
        name = "Shatavari",
        x = 0.69f, 
        y = 0.42f,
        origin = "India",
        description = "Women's health tonic in Ayurvedic tradition, providing hormonal support and improving reproductive health.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 15,
        productId = "32"
    ),
    HerbLocation(
        name = "Triphala",
        x = 0.71f, 
        y = 0.38f,
        origin = "India",
        description = "Three-fruit blend used in Ayurveda for digestion, detoxification, and rejuvenation of the body.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 19,
        productId = "33"
    ),
    HerbLocation(
        name = "Neem",
        x = 0.72f, 
        y = 0.41f,
        origin = "India",
        description = "Powerful antimicrobial herb used for skin conditions, dental health, and as a natural pesticide.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 17,
        productId = "34"
    ),
    HerbLocation(
        name = "Amla",
        x = 0.70f, 
        y = 0.43f,
        origin = "India",
        description = "Indian gooseberry with extremely high vitamin C content, used for immunity, digestion, and hair health.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 22,
        productId = "35"
    ),
    HerbLocation(
        name = "Guduchi",
        x = 0.73f, 
        y = 0.40f,
        origin = "India",
        description = "Immunity-boosting herb used in Ayurveda to fight infections, reduce stress, and support liver function.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 13,
        productId = "36"
    ),
    // Himalayan herbs
    HerbLocation(
        name = "Rhodiola",
        x = 0.68f, 
        y = 0.37f,
        origin = "Himalayas",
        description = "High-altitude adaptogen used to combat fatigue, enhance physical endurance, and improve mental performance.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 12,
        productId = "37"
    ),
    HerbLocation(
        name = "Himalayan Cedar",
        x = 0.69f, 
        y = 0.36f,
        origin = "Himalayas",
        description = "Essential oil source with antiseptic and insect-repellent properties, traditionally used for respiratory issues.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "38"
    ),
    HerbLocation(
        name = "Shilajit",
        x = 0.67f, 
        y = 0.38f,
        origin = "Himalayas",
        description = "Ancient mineral substance collected from Himalayan rocks, used for enhancing vitality and supporting immunological function.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "39"
    ),
    HerbLocation(
        name = "Yartsa Gunbu",
        x = 0.66f, 
        y = 0.37f,
        origin = "Himalayas",
        description = "Rare Himalayan fungus-caterpillar complex highly prized in traditional medicine for energy, stamina, and lung health.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 5,
        productId = "40"
    ),
    HerbLocation(
        name = "Himalayan Thyme",
        x = 0.67f, 
        y = 0.39f,
        origin = "Himalayas",
        description = "Mountain variety of thyme with strong antimicrobial properties, used for respiratory conditions and cooking.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "41"
    ),
    // MORE HERBS TO REACH ~100
    // ADDITIONAL INDIAN HERBS
    HerbLocation(
        name = "Jatamansi",
        x = 0.72f, 
        y = 0.37f,
        origin = "Indian Himalayas",
        description = "Rhizome used in Ayurvedic medicine as a sedative, antidepressant and for promoting hair growth.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "42"
    ),
    HerbLocation(
        name = "Cardamom",
        x = 0.71f, 
        y = 0.44f,
        origin = "Southern India",
        description = "Queen of spices with a strong aroma used in Indian cuisine, desserts, and medicine for digestive health.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 19,
        productId = "43"
    ),
    HerbLocation(
        name = "Curry Leaf",
        x = 0.68f, 
        y = 0.44f,
        origin = "Southern India",
        description = "Aromatic leaf essential in South Indian cuisine that gives dishes a unique citrus-like flavor.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "44"
    ),
    HerbLocation(
        name = "Fenugreek",
        x = 0.70f, 
        y = 0.42f,
        origin = "India",
        description = "Seeds and leaves used in Indian cooking with a maple-like flavor and medicinal properties for digestion.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 17,
        productId = "45"
    ),
    
    // MIDDLE EASTERN HERBS
    HerbLocation(
        name = "Sumac",
        x = 0.56f, 
        y = 0.36f,
        origin = "Middle East",
        description = "Tart, crimson spice made from crushed berries, essential in Middle Eastern cuisines for its lemony flavor.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 12,
        productId = "46"
    ),
    HerbLocation(
        name = "Black Seed",
        x = 0.55f, 
        y = 0.38f,
        origin = "Middle East",
        description = "Small black seeds with medicinal properties, called 'the remedy for everything except death' in Islamic medicine.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "47"
    ),
    HerbLocation(
        name = "Hyssop",
        x = 0.54f, 
        y = 0.36f,
        origin = "Middle East",
        description = "Ancient herb mentioned in the Bible, used for respiratory conditions and as a flavoring in food and liqueurs.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "48"
    ),
    
    // EUROPEAN HERBS
    HerbLocation(
        name = "Angelica",
        x = 0.48f, 
        y = 0.25f,
        origin = "Northern Europe",
        description = "Aromatic herb used in Nordic cuisine and to flavor liqueurs like Chartreuse, with digestive and respiratory benefits.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "49"
    ),
    HerbLocation(
        name = "Borage",
        x = 0.47f, 
        y = 0.31f,
        origin = "Mediterranean",
        description = "Star-shaped blue flowering herb with cucumber-like flavor, used in salads and for skin health.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "50"
    ),
    HerbLocation(
        name = "Melissa",
        x = 0.49f, 
        y = 0.32f,
        origin = "Southern Europe",
        description = "Lemon-scented herb used in teas and medicine for anxiety, insomnia, and digestive issues.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "51"
    ),
    HerbLocation(
        name = "Savory",
        x = 0.51f, 
        y = 0.34f,
        origin = "Mediterranean",
        description = "Peppery herb used to flavor beans and meat dishes in Mediterranean cuisine, with antimicrobial properties.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "52"
    ),
    HerbLocation(
        name = "Lovage",
        x = 0.50f, 
        y = 0.29f,
        origin = "Southern Europe",
        description = "Tall herb with celery-like flavor used in soups and stews, also used medicinally for digestion and joint pain.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "53"
    ),
    HerbLocation(
        name = "St. John's Wort",
        x = 0.46f, 
        y = 0.28f,
        origin = "Europe",
        description = "Yellow-flowered herb traditionally used for depression, anxiety, and wound healing.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 15,
        productId = "54"
    ),
    HerbLocation(
        name = "Mugwort",
        x = 0.52f, 
        y = 0.30f,
        origin = "Europe/Asia",
        description = "Ancient herb used for digestive issues, dream enhancement, and in traditional Chinese medicine.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "55"
    ),
    
    // ASIAN HERBS
    HerbLocation(
        name = "Chrysanthemum",
        x = 0.80f, 
        y = 0.34f,
        origin = "China",
        description = "Flowering herb used in traditional Chinese medicine and as a refreshing tea for reducing fever and inflammation.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 12,
        productId = "56"
    ),
    HerbLocation(
        name = "Licorice Root",
        x = 0.79f, 
        y = 0.32f,
        origin = "China",
        description = "Sweet root used in traditional Chinese medicine to harmonize other herbs and treat respiratory and digestive issues.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "57"
    ),
    HerbLocation(
        name = "Lotus Leaf",
        x = 0.78f, 
        y = 0.39f,
        origin = "East Asia",
        description = "Large circular leaf used in Chinese medicine for weight management and wrapped around food for steaming.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "58"
    ),
    HerbLocation(
        name = "Thai Basil",
        x = 0.75f, 
        y = 0.43f,
        origin = "Thailand",
        description = "Anise-flavored basil essential in Thai cuisine, particularly in curries, stir-fries and soups.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 16,
        productId = "59"
    ),
    HerbLocation(
        name = "Perilla",
        x = 0.81f, 
        y = 0.32f,
        origin = "Japan/Korea",
        description = "Purple-green leaf used in East Asian cuisines for wrapping food, pickling, and as a garnish.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "60"
    ),
    
    // AFRICAN HERBS
    HerbLocation(
        name = "Buchu",
        x = 0.51f, 
        y = 0.64f,
        origin = "South Africa",
        description = "South African herb with blackcurrant flavor used traditionally for urinary tract health and as an antiseptic.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 6,
        productId = "61"
    ),
    HerbLocation(
        name = "Devil's Claw",
        x = 0.52f, 
        y = 0.60f,
        origin = "Southern Africa",
        description = "Root used traditionally for pain relief, particularly for arthritis and inflammatory conditions.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "62"
    ),
    HerbLocation(
        name = "Baobab",
        x = 0.48f, 
        y = 0.50f,
        origin = "Central Africa",
        description = "Fruit from the iconic African tree, rich in vitamin C and used both medicinally and in food preparation.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "63"
    ),
    
    // SOUTH AMERICAN HERBS
    HerbLocation(
        name = "Cat's Claw",
        x = 0.28f, 
        y = 0.58f,
        origin = "Amazon Rainforest",
        description = "Woody vine used by indigenous Amazonian tribes for anti-inflammatory and immune-boosting properties.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "64"
    ),
    HerbLocation(
        name = "Guarana",
        x = 0.30f, 
        y = 0.56f,
        origin = "Brazil",
        description = "Seeds containing high caffeine content, used in energy drinks and traditional medicines in the Amazon.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "65"
    ),
    HerbLocation(
        name = "Maca",
        x = 0.27f, 
        y = 0.60f,
        origin = "Peru",
        description = "High-altitude root crop used for enhancing energy, stamina, and fertility, growing in popularity worldwide.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 13,
        productId = "66"
    ),
    HerbLocation(
        name = "Muira Puama",
        x = 0.29f, 
        y = 0.57f,
        origin = "Brazil",
        description = "Amazonian herb traditionally used for enhancing libido and treating neuromuscular problems.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "67"
    ),
    
    // NORTH AMERICAN HERBS
    HerbLocation(
        name = "Black Cohosh",
        x = 0.23f, 
        y = 0.30f,
        origin = "North America",
        description = "Root used by Native Americans for women's health issues, particularly menopause symptoms.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "68"
    ),
    HerbLocation(
        name = "Goldenseal",
        x = 0.24f, 
        y = 0.31f,
        origin = "Eastern North America",
        description = "Woodland herb used by Native Americans for infections and inflammatory conditions.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 11,
        productId = "69"
    ),
    HerbLocation(
        name = "Wild Bergamot",
        x = 0.22f, 
        y = 0.33f,
        origin = "North America",
        description = "Native American herb with a strong minty-oregano flavor, used for tea and treating colds.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "70"
    ),
    
    // AUSTRALIAN/OCEANIAN HERBS
    HerbLocation(
        name = "Lemon Myrtle",
        x = 0.86f, 
        y = 0.63f,
        origin = "Australia",
        description = "Native Australian herb with the highest natural concentration of citral, used in cooking and aromatherapy.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "71"
    ),
    HerbLocation(
        name = "Kawakawa",
        x = 0.91f, 
        y = 0.70f,
        origin = "New Zealand",
        description = "Sacred Māori herb used for digestive issues, pain relief, and treating wounds and skin conditions.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 5,
        productId = "72"
    ),
    HerbLocation(
        name = "Mountain Pepper",
        x = 0.87f, 
        y = 0.67f,
        origin = "Australia",
        description = "Native Australian spice with a hot, spicy flavor and high antioxidant properties.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "73"
    ),
    
    // OTHER INTERNATIONAL HERBS
    HerbLocation(
        name = "Damiana",
        x = 0.18f, 
        y = 0.42f,
        origin = "Mexico",
        description = "Aromatic shrub traditionally used as an aphrodisiac and for treating anxiety and depression.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "74"
    ),
    HerbLocation(
        name = "Dandelion",
        x = 0.45f, 
        y = 0.26f,
        origin = "Global",
        description = "Common weed whose roots, leaves, and flowers are used medicinally for liver support and as a diuretic.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 15,
        productId = "75"
    ),
    HerbLocation(
        name = "Nettle",
        x = 0.49f, 
        y = 0.24f,
        origin = "Europe/Asia",
        description = "Nutritious wild plant used for allergies, joint pain, and as a spring tonic for detoxification.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 12,
        productId = "76"
    ),
    HerbLocation(
        name = "Sarsaparilla",
        x = 0.25f, 
        y = 0.50f,
        origin = "Central/South America",
        description = "Root traditionally used for skin conditions, joint problems, and as a flavoring in beverages.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "77"
    ),
    HerbLocation(
        name = "Stevia",
        x = 0.28f, 
        y = 0.59f,
        origin = "Paraguay",
        description = "Natural zero-calorie sweetener used as a sugar substitute, with leaves up to 300 times sweeter than sugar.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "78"
    ),
    HerbLocation(
        name = "Vanilla",
        x = 0.19f, 
        y = 0.44f,
        origin = "Mexico",
        description = "Fragrant pods from an orchid, used in baking and perfumery for its sweet, distinct aroma.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 17,
        productId = "79"
    ),
    HerbLocation(
        name = "Wormwood",
        x = 0.50f, 
        y = 0.31f,
        origin = "Europe",
        description = "Bitter herb traditionally used in absinthe production and for treating parasitic infections.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "80"
    ),
    HerbLocation(
        name = "Yarrow",
        x = 0.45f, 
        y = 0.30f,
        origin = "Northern Hemisphere",
        description = "Ancient healing herb used for wounds, fever, and digestive issues across many cultures.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 10,
        productId = "81"
    ),
    HerbLocation(
        name = "Ylang Ylang",
        x = 0.78f, 
        y = 0.52f,
        origin = "Philippines/Indonesia",
        description = "Tropical tree flowers producing essential oil used in perfumes and aromatherapy for stress relief.",
        type = HerbType.AROMATIC,
        imageRes = R.drawable.herb_default,
        vendorsCount = 9,
        productId = "82"
    ),
    HerbLocation(
        name = "Anise",
        x = 0.50f, 
        y = 0.33f,
        origin = "Mediterranean",
        description = "Licorice-flavored seed used in baking, liqueurs, and for digestive support across many cultures.",
        type = HerbType.CULINARY,
        imageRes = R.drawable.herb_default,
        vendorsCount = 13,
        productId = "83"
    ),
    HerbLocation(
        name = "Barberry",
        x = 0.59f, 
        y = 0.33f,
        origin = "Middle East/Central Asia",
        description = "Berries and bark used in Persian cuisine and traditional medicine for liver and gallbladder support.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 8,
        productId = "84"
    ),
    HerbLocation(
        name = "Gentian",
        x = 0.48f, 
        y = 0.32f,
        origin = "European Alps",
        description = "Extremely bitter root used in digestive bitters, aperitifs, and to treat digestive disorders.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 7,
        productId = "85"
    ),
    HerbLocation(
        name = "Moringa",
        x = 0.47f, 
        y = 0.45f,
        origin = "India/Africa",
        description = "Highly nutritious tree leaves used to combat malnutrition and treat various health conditions.",
        type = HerbType.MEDICINAL,
        imageRes = R.drawable.herb_default,
        vendorsCount = 14,
        productId = "86"
    )
)

// Add vendor data model
data class VendorLocation(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val location: String,
    val specialty: String,
    val herbTypes: List<HerbType>,
    val imageRes: Int,
    val rating: Float
)

// Sample vendors from around the world
val vendorLocations = listOf(
    VendorLocation(
        id = "1",
        name = "Willow Creek Farms",
        x = 0.23f,
        y = 0.32f,
        location = "North Carolina, USA",
        specialty = "Medicinal Herbs",
        herbTypes = listOf(HerbType.MEDICINAL, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.8f
    ),
    // Adding Indian and Himalayan vendors
    VendorLocation(
        id = "11",
        name = "Himalayan Herbal Co.",
        x = 0.68f,
        y = 0.37f,
        location = "Nepal",
        specialty = "Rare Mountain Herbs",
        herbTypes = listOf(HerbType.MEDICINAL, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.9f
    ),
    VendorLocation(
        id = "12",
        name = "Ayurvedic Treasures",
        x = 0.71f,
        y = 0.41f,
        location = "Kerala, India",
        specialty = "Traditional Ayurvedic Herbs",
        herbTypes = listOf(HerbType.MEDICINAL),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.7f
    ),
    VendorLocation(
        id = "13",
        name = "Mountain Heights Herbals",
        x = 0.67f,
        y = 0.38f,
        location = "Uttarakhand, India",
        specialty = "Himalayan Medicinals",
        herbTypes = listOf(HerbType.MEDICINAL, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.8f
    ),
    VendorLocation(
        id = "14",
        name = "Delhi Spice Traders",
        x = 0.70f,
        y = 0.39f,
        location = "Delhi, India",
        specialty = "Culinary Herbs & Spices",
        herbTypes = listOf(HerbType.CULINARY),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.6f
    ),
    VendorLocation(
        id = "15",
        name = "Traditional Herb Company",
        x = 0.73f,
        y = 0.42f,
        location = "Mumbai, India",
        specialty = "Medicinal Herb Extracts",
        herbTypes = listOf(HerbType.MEDICINAL),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.5f
    ),
    VendorLocation(
        id = "2",
        name = "Green Valley Herbalists",
        x = 0.21f,
        y = 0.30f,
        location = "Portland, Oregon, USA",
        specialty = "Culinary Herbs",
        herbTypes = listOf(HerbType.CULINARY),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.6f
    ),
    VendorLocation(
        id = "3",
        name = "Himalayan Herb Co.",
        x = 0.69f,
        y = 0.38f,
        location = "Nepal",
        specialty = "Rare Mountain Herbs",
        herbTypes = listOf(HerbType.MEDICINAL),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.9f
    ),
    VendorLocation(
        id = "4",
        name = "Mediterranean Gardens",
        x = 0.48f,
        y = 0.34f,
        location = "Provence, France",
        specialty = "Lavender & Aromatics",
        herbTypes = listOf(HerbType.AROMATIC, HerbType.CULINARY),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.7f
    ),
    VendorLocation(
        id = "5",
        name = "Amazonian Herbs",
        x = 0.29f,
        y = 0.55f,
        location = "Brazil",
        specialty = "Rainforest Botanicals",
        herbTypes = listOf(HerbType.MEDICINAL, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.5f
    ),
    VendorLocation(
        id = "6",
        name = "Kyoto Herb Gardens",
        x = 0.82f,
        y = 0.33f,
        location = "Japan",
        specialty = "Traditional Japanese Herbs",
        herbTypes = listOf(HerbType.CULINARY, HerbType.MEDICINAL),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.8f
    ),
    VendorLocation(
        id = "7",
        name = "Tuscan Flavors",
        x = 0.49f,
        y = 0.34f,
        location = "Italy",
        specialty = "Italian Cooking Herbs",
        herbTypes = listOf(HerbType.CULINARY),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.9f
    ),
    VendorLocation(
        id = "8",
        name = "Spice Road Traders",
        x = 0.56f,
        y = 0.38f,
        location = "Istanbul, Turkey",
        specialty = "Rare & Exotic Spices",
        herbTypes = listOf(HerbType.CULINARY, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.7f
    ),
    VendorLocation(
        id = "9",
        name = "Kerala Ayurvedic Co.",
        x = 0.71f,
        y = 0.44f,
        location = "Kerala, India",
        specialty = "Ayurvedic Herbs",
        herbTypes = listOf(HerbType.MEDICINAL),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.8f
    ),
    VendorLocation(
        id = "10",
        name = "Cape Herb Farms",
        x = 0.52f,
        y = 0.62f,
        location = "South Africa",
        specialty = "African Botanicals",
        herbTypes = listOf(HerbType.MEDICINAL, HerbType.AROMATIC),
        imageRes = R.drawable.vendor_willow_creek,
        rating = 4.5f
    )
)