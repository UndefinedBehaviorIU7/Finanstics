package com.example.finanstics.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.finanstics.presentation.calendar.Calendar
import com.example.finanstics.presentation.groups.Groups
import com.example.finanstics.presentation.settings.Settings
import com.example.finanstics.presentation.stats.Stats
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavGraph(
    pagerState: PagerState,
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> Stats(navController)
                1 -> Groups(navController)
                2 -> Calendar(navController)
                3 -> Settings(navController)
            }
        }
    }
}
