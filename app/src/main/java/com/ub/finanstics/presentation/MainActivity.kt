package com.ub.finanstics.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ub.finanstics.presentation.addAction.AddAction
import com.ub.finanstics.presentation.addAction.AddActionGroup
import com.ub.finanstics.presentation.group.GroupMainScreen
import com.ub.finanstics.presentation.groups.Groups
import com.ub.finanstics.presentation.login.Login
import com.ub.finanstics.presentation.register.Register
import com.ub.finanstics.ui.theme.FinansticsTheme

enum class Navigation(val route: String) {
    STATS("stats"),
    ADD_ACTION("add_action"),
    ADD_ACTION_GROUPS("add_action_groups"),
    GROUPS("groups"),
    CALENDAR("calendar"),
    LOGIN("login"),
    REGISTER("register"),
    SETTINGS("settings"),
    GROUP_STATS("group_stats"),
    GROUP_CALENDAR("group_calendar"),
    GROUP_SETTINGS("group_settings")
}

@Suppress("LongMethod")
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
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
                    composable(Navigation.REGISTER.toString()) {
                        Register(navController)
                    }
                    composable(Navigation.ADD_ACTION.toString()) {
                        AddAction(navController)
                    }
                    composable(Navigation.ADD_ACTION_GROUPS.toString()) {
                        AddActionGroup(navController)
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
