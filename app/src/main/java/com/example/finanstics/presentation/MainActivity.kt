package com.example.finanstics.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.finanstics.presentation.calendar.Calendar
import com.example.finanstics.ui.theme.FinansticsTheme

enum class Navigation(val route: String) {
    MAIN("main"),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinansticsTheme(
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Navigation.MAIN.toString()
                ) {
                    composable(Navigation.MAIN.toString()) {
                        MainScreen(
                            navController = navController
                        )
                    }
                }
          
            }
        }
    }
}
