package com.example.finanstics.presentation.group

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.finanstics.presentation.BottomBar
import com.example.finanstics.presentation.isIn
import com.example.finanstics.presentation.navigation.BottomBarScreen
import com.example.finanstics.presentation.navigation.BottomGroupNavGraph
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun GroupMainScreen(
    navController: NavController,
    initialPage: Int = 0
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val navigationBarColor = MaterialTheme.colorScheme.background

    val pageHistory = remember { mutableStateListOf<Int>() }

    LaunchedEffect(isDarkTheme) {
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = !isDarkTheme
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

    Scaffold(
        bottomBar = { BottomBar(pagerState = pagerState, screens = screens) }
    ) { innerPaddingValues ->
        Box(
            modifier = Modifier.padding(innerPaddingValues)
        ) {
            BottomGroupNavGraph(
                pagerState = pagerState,
                navController = navController
            )
        }
    }
}
