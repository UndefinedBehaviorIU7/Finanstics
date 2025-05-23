package com.ub.finanstics.presentation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.presentation.navigation.BottomBar
import com.ub.finanstics.presentation.navigation.BottomBarScreen
import com.ub.finanstics.presentation.navigation.BottomBarUiState
import com.ub.finanstics.presentation.navigation.BottomBarViewModel
import com.ub.finanstics.presentation.navigation.BottomNavGraph
import com.ub.finanstics.ui.theme.OFFSET_BAR
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ub.finanstics.presentation.settings.profileSettings.ProfileSettingsViewModel
import com.ub.finanstics.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

fun isIn(
    state: Int,
    history: MutableList<Int>
): Boolean {
    for (i in 0..history.size - 1) {
        if (history[i] == state) {
            return true
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Suppress("MagicNumber", "LongMethod")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    initialPage: Int = 0,
    profileVm: ProfileSettingsViewModel = viewModel(),
    themeVm: ThemeViewModel
) {
    val isDarkState = themeVm.isDark.collectAsState()
    val isDark = isDarkState.value

    val systemUiController = rememberSystemUiController()
    val navigationBarColor = MaterialTheme.colorScheme.background

    val pageHistory = remember { mutableStateListOf<Int>() }

    LaunchedEffect(isDark) {
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = !isDark
        )
    }

    val screens = listOf(
        BottomBarScreen.Stats,
        BottomBarScreen.Calendar,
        BottomBarScreen.Settings
    )

    val pagerState = remember { PagerState(pageCount = screens.size) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val targetPage = initialPage.coerceIn(0, screens.size - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
        if (pageHistory.isEmpty()) {
            pageHistory.add(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val newPage = !isIn(
            state = pagerState.currentPage,
            history = pageHistory
        )
        if ((pageHistory.isEmpty() || pageHistory.last() != pagerState.currentPage) && newPage) {
            pageHistory.add(pagerState.currentPage)
        }
    }

    BackHandler(enabled = pageHistory.size > 1) {
        coroutineScope.launch {
            pageHistory.removeLast()
            val prevPage = pageHistory.last()
            pagerState.animateScrollToPage(prevPage)
        }
    }

    val vm: BottomBarViewModel = viewModel()

    Scaffold(
        bottomBar = {}
    ) { innerPaddingValues ->
        Box(
            modifier = Modifier
                .padding(innerPaddingValues)
                .fillMaxSize()
        ) {
            BottomNavGraph(
                pagerState = pagerState,
                navController = navController,
                offsetIcons = if (vm.uiState.collectAsState().value is BottomBarUiState.Hidden)
                    OFFSET_BAR / 2 else OFFSET_BAR * 3 / 2,
                themeVm
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                BottomBar(
                    pagerState = pagerState,
                    screens = screens,
                    vm = vm
                )
            }
        }
    }
}
