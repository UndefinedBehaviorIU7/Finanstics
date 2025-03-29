package com.example.finanstics.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finanstics.presentation.Navigation
import com.example.finanstics.presentation.group.calendar.GroupCalendar
import com.example.finanstics.presentation.group.settings.GroupSettings
import com.example.finanstics.presentation.group.stats.GroupStats
import com.example.finanstics.ui.theme.icons.PersonIcon
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomGroupNavGraph(
    pagerState: PagerState,
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> GroupStats(navController)
                1 -> GroupCalendar(navController)
                2 -> GroupSettings(navController)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 40.dp,
                    horizontal = 20.dp
                )
                .height(60.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Icon(
                imageVector = PersonIcon,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.navigate(Navigation.STATS.toString())
                    }
            )
        }
    }
}
