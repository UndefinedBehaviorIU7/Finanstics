package com.example.finanstics.presentation.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finanstics.presentation.Navigation
import com.example.finanstics.presentation.calendar.Calendar
import com.example.finanstics.presentation.settings.Settings
import com.example.finanstics.presentation.stats.Stats
import com.example.finanstics.ui.theme.icons.CircleIcon
import com.example.finanstics.ui.theme.icons.GroupsIcon
import com.example.finanstics.ui.theme.icons.PlusCircleIcon
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlin.math.abs

@Suppress("MagicNumber", "LongMethod")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavGraph(
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
                0 -> Stats(navController)
                1 -> Calendar(navController)
                2 -> Settings(navController)
            }
        }

        val pageInfo by remember {
            derivedStateOf {
                val offset = pagerState.currentPageOffset
                val current = pagerState.currentPage
                val target = if (offset < 0) current - 1 else current + 1
                Triple(current, target, offset)
            }
        }

        val (currentPage, targetPage, offset) = pageInfo
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        val offsetX by animateDpAsState(
            targetValue = when {
                currentPage == 0 -> 0.dp
                currentPage == 1 && targetPage == 0 -> 0.dp
                currentPage == 1 && targetPage == 2 -> (-100).dp * abs(offset)
                else -> (-100).dp
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        )
                        .height(60.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    GroupsButton(
                        navController = navController
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp
                        )
                        .height(60.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    PlusActionButton(
                        navController = navController,
                        offsetX = offsetX
                    )
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun PlusActionButton(
    navController: NavController,
    offsetX: Dp
) {
    Box(
        modifier = Modifier.offset(x = offsetX)
    ) {
        Icon(
            imageVector = CircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(50.dp)
        )
        Icon(
            imageVector = PlusCircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    // TODO: обработка клика
                }
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupsButton(
    navController: NavController
) {
    Box() {
        Icon(
            imageVector = CircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    TODO()
                }
        )

        Icon(
            imageVector = GroupsIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(50.dp)
                .padding(12.dp)
                .clickable {
                    navController.navigate(Navigation.GROUPS.toString())
                }
        )
    }
}
