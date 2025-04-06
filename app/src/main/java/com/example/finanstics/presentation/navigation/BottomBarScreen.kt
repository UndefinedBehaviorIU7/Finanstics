package com.example.finanstics.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.finanstics.ui.theme.icons.CalendarIcon
import com.example.finanstics.ui.theme.icons.ChartIcon
import com.example.finanstics.ui.theme.icons.SettingsIcon

sealed class BottomBarScreen(
    val icon: ImageVector,
    val page: Int
) {
    object Stats : BottomBarScreen(
        icon = ChartIcon,
        page = 0
    )

    object Calendar : BottomBarScreen(
        icon = CalendarIcon,
        page = 1
    )

    object Settings : BottomBarScreen(
        icon = SettingsIcon,
        page = 2
    )
}
