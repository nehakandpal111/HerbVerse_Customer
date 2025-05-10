package com.example.herbverse_customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.herbverse_customer.auth.AuthRepository
import com.example.herbverse_customer.auth.AuthViewModel
import com.example.herbverse_customer.auth.AuthViewModelFactory
import com.example.herbverse_customer.auth.ui.LoginScreen
import com.example.herbverse_customer.ui.theme.Herbverse_customerTheme
import com.example.herbverse_customer.ui.screens.home.HomeScreen
import com.example.herbverse_customer.ui.screens.product.ProductDetailScreen
import com.example.herbverse_customer.ui.screens.browse.ProductBrowseScreen
import com.example.herbverse_customer.navigation.AppNavigation
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity"
    private var initializationFailed = false
    private var errorMessage = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        
        try {
            setContent {
                Herbverse_customerTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainApp()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fatal error in onCreate", e)
            showFallbackUI("Fatal error: ${e.message}")
        }
        
        Log.d(TAG, "onCreate completed")
    }
    
    @Composable
    private fun MainApp() {
        var isAuthenticated by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }
        
        val authViewModel = remember {
            try {
                val factory = AuthViewModelFactory.getInstance(application)
                ViewModelProvider(this@MainActivity, factory)[AuthViewModel::class.java]
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create AuthViewModel", e)
                showFallbackUI("Failed to create AuthViewModel: ${e.message}")
                null
            }
        }
        
        if (authViewModel == null) {
            ErrorScreen(
                message = "Failed to initialize authentication",
                onRetryClick = { recreate() }
            )
            return
        }
        
        LaunchedEffect(key1 = Unit) {
            isLoading = true
            val autoLogin = authViewModel.checkAutoLogin()
            isAuthenticated = autoLogin
            delay(300)
            isLoading = false
        }
        
        val authState by authViewModel.authState.collectAsState()
        LaunchedEffect(authState) {
            when (authState) {
                AuthViewModel.AuthState.AUTHENTICATED -> {
                    isAuthenticated = true
                    isLoading = false
                }
                AuthViewModel.AuthState.UNAUTHENTICATED -> {
                    isAuthenticated = false
                    isLoading = false
                }
                else -> {
                    isLoading = true
                }
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return
        }
        
        val navController = rememberNavController()
        AppNavigation(
            navController = navController,
            isAuthenticated = isAuthenticated,
            authViewModel = authViewModel
        )
    }
    
    private fun showFallbackUI(errorMsg: String) {
        Log.e(TAG, "Showing fallback UI: $errorMsg")
        errorMessage = errorMsg
        
        setContent {
            Herbverse_customerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ErrorScreen(
                        message = errorMsg,
                        onRetryClick = { recreate() }
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links that arrive after initial launch
        if (intent.action == Intent.ACTION_VIEW) {
            Log.d(TAG, "Deep link received, but using simplified auth. Ignoring link.")
            Toast.makeText(this, "Deep links are not supported in this version.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HERBVERSE",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "Sorry, there was a problem initializing the app:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Button(
            onClick = onRetryClick
        ) {
            Text("Try Again")
        }
    }
}