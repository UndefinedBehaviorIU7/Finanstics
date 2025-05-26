package com.ub.finanstics.presentation.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.ub.finanstics.ui.theme.OFFSET_BAR
import com.ub.finanstics.ui.theme.icons.MinimizeButton
import com.ub.finanstics.ui.theme.icons.MinimizeButtonBg
import kotlinx.coroutines.launch

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomBar(
    pagerState: PagerState,
    screens: List<BottomBarScreen>,
    vm: BottomBarViewModel = viewModel()
) {
    val blocked by vm.blocked.collectAsState()
    if (vm.uiState.collectAsState().value is BottomBarUiState.Visible) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            BarPanel(
                pagerState = pagerState,
                screens = screens,
                vm = vm
            )
            if (!blocked) {
                VisiblePanel(vm)
            }
        }
    }
}

@Suppress("MagicNumber")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BarPanel(
    pagerState: PagerState,
    screens: List<BottomBarScreen>,
    vm: BottomBarViewModel = viewModel()
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
            )
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(top = 1.5.dp)
            .background(color = MaterialTheme.colorScheme.background)
            .height(60.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount > 20f) {
                        vm.hide()
                    }
                }
            },
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
@Composable
fun VisiblePanel(
    vm: BottomBarViewModel = viewModel()
) {
    Row(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
            )
            .height(OFFSET_BAR * 2 + 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Box(
//            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MinimizeButtonBg,
                modifier = Modifier
                    .size(160.dp)
                    .clickable {
                        vm.hide()
                    },
                tint = MaterialTheme.colorScheme.background,
                contentDescription = "",
            )
            Icon(
                imageVector = MinimizeButton,
                modifier = Modifier
                    .size(160.dp)
                    .clickable {
                        vm.hide()
                    },
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = "",
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
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
