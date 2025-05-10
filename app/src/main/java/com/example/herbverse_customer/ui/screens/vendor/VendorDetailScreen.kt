package com.example.herbverse_customer.ui.screens.vendor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.herbverse_customer.R
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme

data class VendorInfo(
    val id: String,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val imageResId: Int,
    val bannerResId: Int,
    val rating: Float,
    val location: String,
    val contactEmail: String,
    val contactPhone: String,
    val specialties: List<String>,
    val sustainabilityInfo: String,
    val foundedYear: Int,
    val website: String,
    val isVerified: Boolean,
    val openForBusiness: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendorId: String,
    onBackClick: () -> Unit
) {
    // In a real app, this would be fetched from a repository
    val vendorInfo = getSampleVendor(vendorId)
    var showVendorLoginDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vendorInfo.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // Add vendor login button
                    IconButton(onClick = { showVendorLoginDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Vendor login",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                // Banner image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(vendorInfo.bannerResId),
                        contentDescription = "${vendorInfo.name} farm",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Gradient overlay for better text visibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                    
                    // Status badge
                    VendorStatusBadge(
                        isVerified = vendorInfo.isVerified,
                        isOpen = vendorInfo.openForBusiness,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                    
                    // Vendor logo/profile image
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center)
                    ) {
                        Image(
                            painter = painterResource(vendorInfo.imageResId),
                            contentDescription = vendorInfo.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                // Vendor name and description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = vendorInfo.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (vendorInfo.isVerified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified vendor",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = vendorInfo.shortDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Rating stars
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < vendorInfo.rating) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Text(
                            text = " ${vendorInfo.rating}/5",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Quick action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showContactDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Contact Vendor")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            if (vendorInfo.website.isNotEmpty()) {
                                uriHandler.openUri(vendorInfo.website)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Visit Website")
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
                
                // Contact information
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = vendorInfo.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            // Open email app
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${vendorInfo.contactEmail}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = vendorInfo.contactEmail,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            // Open phone app
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${vendorInfo.contactPhone}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No phone app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = vendorInfo.contactPhone,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Founded: ${vendorInfo.foundedYear}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // About the vendor
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About ${vendorInfo.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = vendorInfo.fullDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // Specialties
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Specialties",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Display specialties as chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(vendorInfo.specialties) { specialty ->
                            SuggestionChip(
                                onClick = { /* do nothing */ },
                                label = { Text(specialty) }
                            )
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // Sustainability information
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sustainability Commitment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = vendorInfo.sustainabilityInfo,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Visit vendor button
                Button(
                    onClick = { /* Navigate to vendor products */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Browse ${vendorInfo.name}'s Products")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Vendor login dialog
    if (showVendorLoginDialog) {
        VendorLoginDialog(
            onDismiss = { showVendorLoginDialog = false },
            onLoginClick = {
                // In a real app, navigate to vendor portal
                Toast.makeText(context, "Vendor portal coming soon!", Toast.LENGTH_SHORT).show()
                showVendorLoginDialog = false
            }
        )
    }
    
    // Contact dialog
    if (showContactDialog) {
        VendorContactDialog(
            vendorInfo = vendorInfo,
            onDismiss = { showContactDialog = false }
        )
    }
}

@Composable
fun VendorStatusBadge(
    isVerified: Boolean,
    isOpen: Boolean,
    modifier: Modifier = Modifier
) {
    val (color, text) = if (isOpen) {
        Pair(Color(0xFF4CAF50), "Open for Orders")
    } else {
        Pair(Color(0xFFE57373), "Temporarily Closed")
    }
    
    Surface(
        color = color.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun VendorLoginDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Vendor Login",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Are you the owner of this shop? Log in to manage your products and orders.",
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Login")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(onClick = { /* Navigate to register */ }) {
                    Text("Register as a vendor")
                }
            }
        }
    }
}

@Composable
fun VendorContactDialog(
    vendorInfo: VendorInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
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
                Text(
                    text = "Contact ${vendorInfo.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Choose how you'd like to reach out:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${vendorInfo.contactPhone}")
                        }
                        try {
                            context.startActivity(intent)
                            onDismiss()
                        } catch (e: Exception) {
                            Toast.makeText(context, "No phone app found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${vendorInfo.contactEmail}")
                        }
                        try {
                            context.startActivity(intent)
                            onDismiss()
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Email")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun getSampleVendor(vendorId: String): VendorInfo {
    return when (vendorId) {
        "1" -> VendorInfo(
            id = "1",
            name = "Willow Creek Farms",
            shortDescription = "Fourth-generation organic herb farmers",
            fullDescription = "Willow Creek Farms has been growing organic herbs for over 75 years. Our family farm " +
                "started with just a small plot of land and has grown into a 50-acre operation dedicated to cultivating " +
                "the finest herbs using sustainable farming practices. We specialize in heirloom varieties and rare " +
                "medicinal herbs that are difficult to find elsewhere.",
            imageResId = R.drawable.vendor_willow_creek,
            bannerResId = R.drawable.banner_fresh_herbs,
            rating = 4.8f,
            location = "Blue Ridge Mountains, North Carolina",
            contactEmail = "info@willowcreekfarms.com",
            contactPhone = "(828) 555-1234",
            specialties = listOf("Medicinal Herbs", "Organic", "Heirloom Varieties", "Rare Species"),
            sustainabilityInfo = "We use no chemical pesticides or fertilizers. Our farm operates on 100% renewable " +
                "energy and employs regenerative agriculture practices that improve soil health year after year. " +
                "All of our packaging is recyclable or compostable.",
            foundedYear = 1948,
            website = "https://www.willowcreekfarms.example.com",
            isVerified = true,
            openForBusiness = true
        )
        else -> VendorInfo(
            id = "2",
            name = "Green Valley Herbalists",
            shortDescription = "Urban herb farm with a sustainable mission",
            fullDescription = "Green Valley Herbalists started as an urban farming project in 2010 and has grown into " +
                "a thriving business that supplies herbs to local restaurants and markets. We believe in connecting " +
                "people with the plants they use daily and educating our community about sustainable growing practices.",
            imageResId = R.drawable.vendor_willow_creek,  // Use existing resource
            bannerResId = R.drawable.banner_fresh_herbs,
            rating = 4.6f,
            location = "Portland, Oregon",
            contactEmail = "hello@greenvalleyherbalists.com",
            contactPhone = "(503) 555-6789",
            specialties = listOf("Culinary Herbs", "Microgreens", "Urban Farming", "Educational Programs"),
            sustainabilityInfo = "Our urban farm uses vertical growing systems and hydroponics to maximize space " +
                "efficiency while minimizing water use. We compost all plant waste and use it to fertilize our crops. " +
                "Our delivery vehicles are electric to reduce our carbon footprint.",
            foundedYear = 2010,
            website = "https://www.greenvalleyherbalists.example.com",
            isVerified = false,
            openForBusiness = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VendorDetailScreenPreview() {
    Herbverse_customerTheme {
        Surface {
            VendorDetailScreen(
                vendorId = "1",
                onBackClick = {}
            )
        }
    }
}