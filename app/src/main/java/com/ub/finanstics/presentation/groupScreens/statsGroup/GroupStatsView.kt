package com.ub.finanstics.presentation.groupScreens.statsGroup

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.userScreens.calendar.MonthNameClass
import com.ub.finanstics.presentation.userScreens.stats.PieChart
import com.ub.finanstics.presentation.templates.AvatarBitmap
import com.ub.finanstics.presentation.templates.Divider
import com.ub.finanstics.presentation.templates.Loader
import com.ub.finanstics.ui.theme.OFFSET_BAR

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun GroupStats(
    navController: NavController,
    isVisible: Boolean = true
) {
    val vm: GroupStatsViewModel = viewModel()
    val dvm: GroupDetailsViewModel = viewModel()

    val detState by dvm.uiState.collectAsState()

    LaunchedEffect(isVisible) {
        if (isVisible) {
            vm.autoUpdate()
        } else {
            vm.cancelUpdate()
        }
    }

    DisposableEffect(Unit) {
        onDispose { vm.cancelUpdate() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(top = 13.dp),
    ) {
        Row(
            modifier = if (detState is GroupDetailsUiState.DetailedAction)
                Modifier.blur(20.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded) else Modifier
        ) {
            when (val uiState = vm.uiState.collectAsState().value) {
                GroupStatsUiState.Loading -> {
                    BoxWithConstraints {
                        val width = maxWidth
                        Loader(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(width / 3)
                        )
                    }
                }

                is GroupStatsUiState.Calendar -> {
                    val calendar = uiState.calendar
                    Column {
                        Header(
                            isClicked = uiState.all,
                            vm = vm,
                            dvm = dvm,
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm,
                        )
                    }
                }

                is GroupStatsUiState.LoadingData -> {
                    val calendar = uiState.calendar
                    Column {
                        Header(
                            isClicked = uiState.all,
                            vm = vm,
                            dvm = dvm,
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm
                        )
                        BoxWithConstraints {
                            val width = maxWidth
                            Loader(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(width / 3)
                            )
                        }
                    }
                }

                is GroupStatsUiState.Done -> {
                    val calendar = uiState.calendar
                    Column(modifier = Modifier) {
                        Header(
                            isClicked = uiState.all,
                            vm = vm,
                            dvm = dvm,
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm
                        )
                        GroupStatsView(
                            vm,
                            dvm
                        )
                    }
                }

                is GroupStatsUiState.Error -> GroupStatsErrorView(uiState.message)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun Header(
    isClicked: Boolean,
    vm: GroupStatsViewModel = viewModel(),
    dvm: GroupDetailsViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp,
                end = 20.dp
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.navigate(Navigation.GROUPS.toString()) },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }

        val image = vm.groupImage
        AvatarBitmap(
            image = image,
            contentStr = stringResource(R.string.group_picture),
            modifier = Modifier.size(40.dp),
            resource = R.drawable.placeholder
        )
        Spacer(modifier = Modifier.weight(0.1f))

        Text(
            text = vm.groupName,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            onClick = {
                vm.switchAll()
                dvm.changeAllTime()
                dvm.hideDetailedActions()
                vm.fetchData()
            },
            colors = if (!isClicked) ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary
            ) else ButtonDefaults.buttonColors(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                color = if (!isClicked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.background,
                fontSize = 16.sp,
                text = stringResource(R.string.All_time)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun GroupStatsView(
    vm: GroupStatsViewModel = viewModel(),
    dvm: GroupDetailsViewModel
) {
    val incomes by vm.incomes.collectAsState()
    val expenses by vm.expenses.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            GroupStatsViewVertical(
                incomes = incomes,
                expenses = expenses,
                vm = vm,
                dvm = dvm
            )
        } else {
            GroupStatsViewHorizontal(
                incomes = incomes,
                expenses = expenses,
                vm = vm,
                dvm = dvm
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun GroupStatsViewVertical(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: GroupStatsViewModel,
    dvm: GroupDetailsViewModel,
) {
    val uiState = vm.uiState.collectAsState().value
    val windowSize = calculateWindowSizeClass(activity = LocalContext.current as ComponentActivity)

    val radiusPie = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 80.dp
        WindowWidthSizeClass.Medium -> 85.dp
        WindowWidthSizeClass.Expanded -> 90.dp
        else -> 80.dp
    }

    val charWidth = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 20.dp
        WindowWidthSizeClass.Medium -> 22.dp
        WindowWidthSizeClass.Expanded -> 26.dp
        else -> 20.dp
    }

    if (uiState is GroupStatsUiState.Done) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        ) {
            item {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        PieChart(
                            data = incomes,
                            radiusOuter = radiusPie,
                            expenses = false,
                            chartBarWidth = charWidth,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        PieChart(
                            data = expenses,
                            radiusOuter = radiusPie,
                            expenses = true,
                            chartBarWidth = charWidth,
                        )
                    }
                }
            }
            item { Divider(10.dp, 2.dp) }

            item {
                GroupBalance(
                    currentBalance = vm.balance(incomes, expenses),
                    period = if (uiState.all) stringResource(R.string.all_time)
                    else MonthNameClass.str(uiState.calendar.getData().getMonth()),
                    uiState.totalBalance
                )
            }

            item { Divider(10.dp, 2.dp) }

            item {
                GroupDetailsPieChart(
                    data = incomes,
                    date = uiState.calendar,
                    expenses = false,
                    vm = dvm
                )
            }
            item { Divider(10.dp, 2.dp) }
            item {
                GroupDetailsPieChart(
                    data = expenses,
                    date = uiState.calendar,
                    expenses = true,
                    vm = dvm
                )
            }
            item { Spacer(modifier = Modifier.height(OFFSET_BAR + 60.dp)) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun GroupStatsViewHorizontal(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: GroupStatsViewModel,
    dvm: GroupDetailsViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    if (uiState is GroupStatsUiState.Done) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.5f)) {
                        PieChart(
                            data = incomes,
                            radiusOuter = 90.dp,
                            expenses = false,
                            chartBarWidth = 26.dp,
                        )
                    }
                    Column(modifier = Modifier.weight(0.5f)) {
                        PieChart(
                            data = expenses,
                            radiusOuter = 90.dp,
                            expenses = true,
                            chartBarWidth = 26.dp,
                        )
                    }
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                LazyColumn {
                    item {
                        GroupDetailsPieChart(
                            data = incomes,
                            date = uiState.calendar,
                            expenses = false,
                            vm = dvm
                        )
                    }
                    item { Divider(10.dp, 2.dp) }

                    item {
                        GroupBalance(
                            currentBalance = vm.balance(incomes, expenses),
                            period = if (uiState.all) stringResource(R.string.all_time)
                            else MonthNameClass.str(uiState.calendar.getData().getMonth()),
                            uiState.totalBalance
                        )
                    }

                    item { Divider(10.dp, 2.dp) }
                    item {
                        GroupDetailsPieChart(
                            data = expenses,
                            date = uiState.calendar,
                            expenses = true,
                            vm = dvm
                        )
                    }
                    item { Spacer(modifier = Modifier.height(50.dp)) }
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupStatsErrorView(
    message: String
) {
    Text(
        text = message,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.primary
    )
}
