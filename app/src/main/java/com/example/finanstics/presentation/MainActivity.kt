package com.example.finanstics.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finanstics.presentation.group.GroupMainScreen
import com.example.finanstics.presentation.groups.Groups
import com.example.finanstics.presentation.login.Login
import com.example.finanstics.ui.theme.FinansticsTheme

enum class Navigation(val route: String) {
    STATS("stats"),
    GROUPS("groups"),
    CALENDAR("calendar"),
    LOGIN("login"),
    SETTINGS("settings"),
    GROUP_STATS("group_stats"),
    GROUP_CALENDAR("group_calendar"),
    GROUP_SETTINGS("group_settings")
}

@Suppress("LongMethod")
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
                    startDestination = Navigation.STATS.toString()
                ) {
                    composable(Navigation.STATS.toString()) {
                        MainScreen(
                            navController = navController,
                            initialPage = 0
                        )
                    }
                    composable(Navigation.LOGIN.toString()) {
                        Login(navController)
                    }
                    composable(Navigation.GROUPS.toString()) {
                        Groups(
                            navController = navController
                        )
                    }
                    composable(Navigation.CALENDAR.toString()) {
                        MainScreen(
                            navController = navController,
                            initialPage = 1
                        )
                    }
                    composable(Navigation.SETTINGS.toString()) {
                        MainScreen(
                            navController = navController,
                            initialPage = 2
                        )
                    }
                    composable(Navigation.GROUP_STATS.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 0
                        )
                    }
                    composable(Navigation.GROUP_CALENDAR.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 1
                        )
                    }
                    composable(Navigation.GROUP_SETTINGS.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 2
                        )
                    }
                }
            }
        }
    }
}
