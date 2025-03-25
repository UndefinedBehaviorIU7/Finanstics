package com.example.finanstics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    val screens = listOf(
        BottomBarScreen.Stats,
        BottomBarScreen.Groups,
        BottomBarScreen.Calendar,
        BottomBarScreen.Settings
    )

    val pagerState = PagerState(pageCount = screens.size)

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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomBar(
    pagerState: PagerState,
    screens: List<BottomBarScreen>
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background)
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
            .height(60.dp)
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
