package com.example.herbverse_customer.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.herbverse_customer.R
import com.example.herbverse_customer.data.model.Product
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerbQuizScreen(
    onBackClick: () -> Unit = {},
    onProductRecommended: (String) -> Unit = {}
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }
    var recommendedHerbs by remember { mutableStateOf<List<Product>>(emptyList()) }
    val answers = remember { mutableMapOf<Int, String>() }
    
    val questions = listOf(
        QuizQuestion(
            question = "What are you primarily looking for in an herb?",
            options = listOf("Culinary use", "Medicinal benefits", "Aromatherapy", "Decoration")
        ),
        QuizQuestion(
            question = "How would you describe your preferred flavor profile?",
            options = listOf("Sweet", "Savory", "Bitter", "Spicy", "Mild")
        ),
        QuizQuestion(
            question = "How much experience do you have with herbs?",
            options = listOf("Beginner", "Some experience", "Experienced", "Expert")
        ),
        QuizQuestion(
            question = "Where do you plan to use these herbs?",
            options = listOf("Indoor garden", "Outdoor garden", "Windowsill", "Container garden")
        ),
        QuizQuestion(
            question = "How much time can you dedicate to maintenance?",
            options = listOf("Minimal", "Weekly care", "Daily attention", "Extensive care")
        )
    )
    
    val progress = if (quizCompleted) 1f else (currentQuestionIndex.toFloat() / questions.size.toFloat())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (quizCompleted) "Your Herb Matches" else "Herb Quiz") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            
            if (quizCompleted) {
                // Results screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Based on your preferences, here are your perfect herb matches:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    items(recommendedHerbs) { herb ->
                        RecommendedHerbCard(
                            herb = herb,
                            onHerbSelected = { onProductRecommended(herb.id) }
                        )
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = {
                                // Reset quiz
                                currentQuestionIndex = 0
                                quizCompleted = false
                                answers.clear()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Take Quiz Again")
                        }
                    }
                }
            } else {
                // Quiz questions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val currentQuestion = questions[currentQuestionIndex]
                    
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    currentQuestion.options.forEach { option ->
                        val isSelected = answers[currentQuestionIndex] == option
                        
                        OptionItem(
                            text = option,
                            isSelected = isSelected,
                            onClick = {
                                answers[currentQuestionIndex] = option
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (currentQuestionIndex > 0) {
                            OutlinedButton(
                                onClick = { currentQuestionIndex-- },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Previous")
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        
                        Button(
                            onClick = {
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    quizCompleted = true
                                    recommendedHerbs = getRecommendedHerbs(answers)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = answers.containsKey(currentQuestionIndex)
                        ) {
                            Text(if (currentQuestionIndex == questions.size - 1) "See Results" else "Next")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RecommendedHerbCard(
    herb: Product,
    onHerbSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHerbSelected() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = getImageResourceForHerb(herb.id)),
                    contentDescription = herb.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = herb.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = herb.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                
                Text(
                    text = "Match score: 95%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = onHerbSelected,
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

// Function to get herb image based on ID
fun getImageResourceForHerb(herbId: String): Int {
    return when (herbId) {
        "1" -> R.drawable.herb_lavender
        "2" -> R.drawable.herb_basil
        "3" -> R.drawable.herb_chamomile
        "4" -> R.drawable.herb_rosemary
        "5" -> R.drawable.herb_peppermint
        "6" -> R.drawable.herb_echinacea
        else -> R.drawable.herb_default
    }
}

// Function to get recommended herbs based on answers
fun getRecommendedHerbs(answers: Map<Int, String>): List<Product> {
    try {
        // In a real app, this would use a more sophisticated recommendation algorithm
        // based on the user's answers. For now, we'll just return a few sample herbs.
        val primaryUse = answers[0] ?: ""
        val flavorProfile = answers[1] ?: ""
        
        val recommendedHerbs = mutableListOf<Product>()
        
        when {
            primaryUse.contains("Culinary") -> {
                recommendedHerbs.add(
                    Product(
                        id = "2",
                        name = "Basil",
                        shortDescription = "Perfect for Italian dishes and Mediterranean cuisine",
                        fullDescription = "Basil is a culinary herb belonging to the mint family. It adds fresh flavor to many dishes and is especially popular in Italian and Mediterranean cuisine.",
                        price = 4.99,
                        stock = 100,
                        categoryId = "2",
                        imageUrl = ""
                    )
                )
                recommendedHerbs.add(
                    Product(
                        id = "4",
                        name = "Rosemary",
                        shortDescription = "Great for roasted meats and savory dishes",
                        fullDescription = "Rosemary is a fragrant evergreen herb with needle-like leaves. It's used as a culinary condiment, to make bodily perfumes, and for its health benefits.",
                        price = 5.99,
                        stock = 70,
                        categoryId = "2",
                        imageUrl = ""
                    )
                )
            }
            primaryUse.contains("Medicinal") -> {
                recommendedHerbs.add(
                    Product(
                        id = "3",
                        name = "Chamomile",
                        shortDescription = "Herbal remedy for relaxation and sleep",
                        fullDescription = "Chamomile is known for its soothing properties and commonly used in teas to help with sleep and digestive issues.",
                        price = 7.99,
                        stock = 80,
                        categoryId = "1",
                        imageUrl = ""
                    )
                )
                recommendedHerbs.add(
                    Product(
                        id = "6",
                        name = "Echinacea",
                        shortDescription = "Boosts immune system and fights infections",
                        fullDescription = "Echinacea is a flowering plant that is native to North America. It is commonly used to boost the immune system and fight off infections. Our echinacea is grown using sustainable farming practices and is carefully harvested to ensure maximum potency.",
                        price = 7.99,
                        stock = 75,
                        categoryId = "1",
                        imageUrl = ""
                    )
                )
            }
            primaryUse.contains("Aromatherapy") -> {
                recommendedHerbs.add(
                    Product(
                        id = "1",
                        name = "Lavender",
                        shortDescription = "Fresh aromatic herb with soothing properties",
                        fullDescription = "Lavender is well known for its fragrance and medicinal properties. It promotes relaxation and can help with sleep, anxiety, and more. Our lavender is organically grown without pesticides or artificial fertilizers, ensuring you get the purest product possible.",
                        price = 9.99,
                        stock = 50,
                        categoryId = "3",
                        imageUrl = ""
                    )
                )
                recommendedHerbs.add(
                    Product(
                        id = "5",
                        name = "Peppermint",
                        shortDescription = "Cooling herb with refreshing aroma",
                        fullDescription = "Peppermint is a hybrid mint, a cross between watermint and spearmint. The plant is widely used in teas and as a flavoring agent.",
                        price = 6.99,
                        stock = 90,
                        categoryId = "1",
                        imageUrl = ""
                    )
                )
            }
            else -> {
                // Default recommendations
                recommendedHerbs.add(
                    Product(
                        id = "1",
                        name = "Lavender",
                        shortDescription = "Fresh aromatic herb with soothing properties",
                        fullDescription = "Lavender is well known for its fragrance and medicinal properties. It promotes relaxation and can help with sleep, anxiety, and more.",
                        price = 9.99,
                        stock = 50,
                        categoryId = "3",
                        imageUrl = ""
                    )
                )
                recommendedHerbs.add(
                    Product(
                        id = "2",
                        name = "Basil",
                        shortDescription = "Fresh culinary herb with a sweet aroma",
                        fullDescription = "Basil is a culinary herb of the family Lamiaceae. It is a tender plant, used in cuisines worldwide.",
                        price = 4.99,
                        stock = 100,
                        categoryId = "2",
                        imageUrl = ""
                    )
                )
            }
        }
        
        return recommendedHerbs
    } catch (e: Exception) {
        android.util.Log.e("HerbQuiz", "Error getting recommendations: ${e.message}", e)
        // Return a fallback recommendation if something goes wrong
        return listOf(
            Product(
                id = "2",
                name = "Basil",
                shortDescription = "Fresh culinary herb with a sweet aroma",
                fullDescription = "Basil is a culinary herb of the family Lamiaceae. It is a tender plant, used in cuisines worldwide.",
                price = 4.99,
                stock = 100,
                categoryId = "2",
                imageUrl = ""
            )
        )
    }
}

data class QuizQuestion(
    val question: String,
    val options: List<String>
)

@Preview(showBackground = true)
@Composable
fun HerbQuizPreview() {
    Herbverse_customerTheme {
        Surface {
            HerbQuizScreen()
        }
    }
}