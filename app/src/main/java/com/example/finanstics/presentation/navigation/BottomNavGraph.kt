package com.example.finanstics.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.finanstics.presentation.calendar.Calendar
import com.example.finanstics.presentation.settings.Settings
import com.example.finanstics.presentation.stats.Stats
import com.example.finanstics.ui.theme.icons.Circle
import com.example.finanstics.ui.theme.icons.GroupsIcon
import com.example.finanstics.ui.theme.icons.PlusCircle
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
            Box()
            {
                Icon(
                    imageVector = Circle,
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
                        .padding(5.dp)
                        .clickable {
                            navController.navigate(Navigation.GROUPS.toString())
                        }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            Row( modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 40.dp,
                    horizontal = 20.dp
                )
                .height(60.dp),
                horizontalArrangement = Arrangement.End) {
                Box(){
                    Icon(
                        imageVector = Circle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                TODO()
                            }
                    )

                    Icon(
                        imageVector = PlusCircle,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                TODO()
                            }
                    )
                }
            }
        }

    }
}
