package com.ub.finanstics.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.ub.finanstics.R
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
        askNotificationPermission()
        createNotificationChannel()
        val initialized = FirebaseApp.getApps(this).isNotEmpty()
        Log.d("FirebaseInit", "Is FirebaseApp initialized? $initialized")
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "FCM can't post notifications without POST_NOTIFICATIONS permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
