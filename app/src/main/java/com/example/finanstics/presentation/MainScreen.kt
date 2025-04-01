package com.example.finanstics.presentation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finanstics.presentation.navigation.BottomBarScreen
import com.example.finanstics.presentation.navigation.BottomNavGraph
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
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
        if ((pageHistory.isEmpty() || pageHistory.last() != pagerState.currentPage) && !isIn(
            state = pagerState.currentPage,
            history = pageHistory
        )) {
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
            BottomNavGraph(
                pagerState = pagerState,
                navController = navController
            )
        }
    }
}

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomBar(
    pagerState: PagerState,
    screens: List<BottomBarScreen>
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
            )
            .height(60.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEach { screen ->
            BarItem(
                screen = screen,
                pagerState = pagerState
            )
        }
    }
}

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BarItem(
    screen: BottomBarScreen,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    val selected = pagerState.currentPage == screen.page

    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(CircleShape)
            .clickable(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(screen.page)
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                    horizontal = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = "",
                tint = if (selected) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.primary,
            )
        }
    }
}
