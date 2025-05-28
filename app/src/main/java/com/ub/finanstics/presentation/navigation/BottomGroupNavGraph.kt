package com.ub.finanstics.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.calendar.CalendarGroup
import com.ub.finanstics.presentation.settings.groupSettings.GroupSettings
import com.ub.finanstics.presentation.statsGroup.GroupStats
import com.ub.finanstics.ui.theme.OFFSET_BAR
import com.ub.finanstics.ui.theme.ThemeViewModel
import com.ub.finanstics.ui.theme.icons.CircleIcon
import com.ub.finanstics.ui.theme.icons.PersonIcon
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomGroupNavGraph(
    pagerState: PagerState,
    navController: NavController,
    offsetIcons: Dp,
    vm: BottomBarViewModel,
    themeVm: ThemeViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        HorizontalPager(state = pagerState) { page ->
            val isVisible = page == pagerState.currentPage
            when (page) {
                0 -> GroupStats(navController, isVisible)
                1 -> CalendarGroup(isVisible)
                2 -> GroupSettings(navController)
            }
            if (pagerState.currentPage == 2) {
                vm.show(OFFSET_BAR)
                vm.block()
            } else {
                vm.unblock()
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
                .fillMaxSize()
                .padding(bottom = offsetIcons)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(60.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (uiState is BottomBarUiState.Visible) {
                        PersonButton(
                            navController = navController,
                            offsetX = offsetX
                        )
                    } else {
                        MenuButton(
                            onClick = { vm.show(OFFSET_BAR) },
                            offsetX = offsetX
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(60.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    PlusActionButton(
                        onClick = {
                            navController.navigate(Navigation.ADD_ACTION_GROUPS.toString())
                                  },
                        offsetX = offsetX
                    )
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun PersonButton(
    offsetX: Dp,
    navController: NavController
) {
    Box(modifier = Modifier.offset(offsetX)) {
        Icon(
            imageVector = CircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(50.dp)
        )

        Icon(
            imageVector = PersonIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(50.dp)
                .padding(12.dp)
                .clickable {
                    navController.navigate(Navigation.STATS.toString())
                }
        )
    }
}
