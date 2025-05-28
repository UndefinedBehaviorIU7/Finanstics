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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.ub.finanstics.R
import com.ub.finanstics.presentation.addAction.AddAction
import com.ub.finanstics.presentation.addActionGroup.AddActionGroup
import com.ub.finanstics.presentation.addGroup.AddGroupScreen
import com.ub.finanstics.presentation.group.GroupMainScreen
import com.ub.finanstics.presentation.groups.Groups
import com.ub.finanstics.presentation.login.Login
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import com.ub.finanstics.presentation.register.Register
import com.ub.finanstics.ui.theme.FinansticsTheme
import com.ub.finanstics.ui.theme.ThemeViewModel

enum class Navigation {
    STATS,
    ADD_ACTION,
    ADD_ACTION_GROUPS,
    ADD_GROUP,
    GROUPS,
    CALENDAR,
    LOGIN,
    REGISTER,
    SETTINGS,
    GROUP_STATS,
    GROUP_CALENDAR,
    GROUP_SETTINGS
}

@Suppress("LongMethod")
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    companion object {
        private const val KEY_NOTIFICATIONS_ASKED = "notifications_asked"
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermissionOnce()
        createNotificationChannel()
        val initialized = FirebaseApp.getApps(this).isNotEmpty()
        Log.d("FirebaseInit", "Is FirebaseApp initialized? $initialized")
        enableEdgeToEdge()

        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val isDark by themeVm.isDark.collectAsState()

            FinansticsTheme(
                dynamicColor = false,
                darkTheme = isDark
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Navigation.STATS.toString()
                ) {
                    composable(Navigation.STATS.toString()) {
                        MainScreen(
                            navController = navController,
                            initialPage = 0,
                            themeVm = themeVm
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
                            initialPage = 1,
                            themeVm = themeVm
                        )
                    }
                    composable(Navigation.SETTINGS.toString()) {
                        MainScreen(
                            navController = navController,
                            initialPage = 2,
                            themeVm = themeVm
                        )
                    }
                    composable(Navigation.GROUP_STATS.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 0,
                            themeVm
                        )
                    }
                    composable(Navigation.GROUP_CALENDAR.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 1,
                            themeVm
                        )
                    }
                    composable(Navigation.GROUP_SETTINGS.toString()) {
                        GroupMainScreen(
                            navController = navController,
                            initialPage = 2,
                            themeVm
                        )
                    }
                    composable(Navigation.ADD_GROUP.toString()) {
                        AddGroupScreen(navController)
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
                    "Для уведомлений предоставьте разрешение в настройках",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun askNotificationPermissionOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val prefs = PreferencesManager(application)
        val alreadyAsked = prefs.getBoolean(KEY_NOTIFICATIONS_ASKED, false)
        if (!alreadyAsked) {
            prefs.saveData(KEY_NOTIFICATIONS_ASKED, true)

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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
