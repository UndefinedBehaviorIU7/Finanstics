package com.ub.finanstics.presentation.stats

import android.content.res.Configuration
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.calendar.MonthNameClass
import com.ub.finanstics.ui.theme.Divider
import com.ub.finanstics.ui.theme.Loader

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun Stats(
    navController: NavController,
    isVisible: Boolean = true
) {
    val vm: StatsViewModel = viewModel()
    val dvm: DetailsViewModel = viewModel()
    val incomes by vm.incomes.collectAsState()
    val expenses by vm.expenses.collectAsState()

    LaunchedEffect(isVisible) {
        if (isVisible) {
            vm.autoUpdate()
        } else {
            vm.cancelUpdate()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.cancelUpdate()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(
                top = 25.dp,
                start = 20.dp,
                end = 20.dp,
            )
    ) {
        Row() {
            when (val uiState = vm.uiState.collectAsState().value) {
                StatsUiState.Loading -> {
                    Loader(
                        modifier = Modifier
                    )
                }

                is StatsUiState.Calendar -> {
                    val calendar = uiState.calendar
                    Column() {
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm
                        )
                    }
                }

                is StatsUiState.LoadingData -> {
                    val calendar = uiState.calendar
                    Column() {
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm
                        )
                        Loader(
                            modifier = Modifier
                        )
                    }
                }

                is StatsUiState.Done -> {
                    val calendar = uiState.calendar
                    Column(modifier = Modifier) {
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm,
                            dvm = dvm
                        )
                        StatsView(
                            incomes,
                            expenses,
                            vm,
                            dvm
                        )
                    }
                }

                is StatsUiState.Error -> StatsErrorView(uiState.message)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Composable
fun StatsView(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel = viewModel(),
    dvm: DetailsViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            StatsViewVertical(
                incomes = incomes,
                expenses = expenses,
                vm = vm,
                dvm = dvm
            )
        } else {
            StatsViewHorizontal(
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
fun StatsViewVertical(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel,
    dvm: DetailsViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    val date by vm.date.collectAsState()
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

    if (uiState is StatsUiState.Done) {
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
                Balance(
                    vm.balance(incomes, expenses),
                    MonthNameClass.str(uiState.calendar.getData().getMonth()),
                    -uiState.totalBalance
                )
            }
            item { Divider(10.dp, 2.dp) }
            item {
                DetailsPieChart(
                    vm = dvm,
                    data = incomes,
                    date = date.deepCopy(),
                    expenses = false
                )
            }
            item { Divider(10.dp, 2.dp) }
            item {
                DetailsPieChart(
                    vm = dvm,
                    data = expenses,
                    date = date.deepCopy(),
                    expenses = true
                )
            }
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber", "LongMethod")
@Composable
fun StatsViewHorizontal(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel,
    dvm: DetailsViewModel,
) {
    val uiState = vm.uiState.collectAsState().value
    if (uiState is StatsUiState.Done) {
        Row() {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn {
                    item {
                        DetailsPieChart(
                            vm = dvm,
                            data = incomes,
                            date = uiState.calendar,
                            expenses = false
                        )
                    }
                    item { Divider(10.dp, 2.dp) }

                    item {
                        Balance(
                            vm.balance(incomes, expenses),
                            MonthNameClass.str(uiState.calendar.getData().getMonth()),
                            uiState.totalBalance
                        )
                    }

                    item { Divider(10.dp, 2.dp) }
                    item {
                        DetailsPieChart(
                            vm = dvm,
                            data = expenses,
                            date = uiState.calendar,
                            expenses = true
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
fun StatsErrorView(
    message: String
) {
    Text(
        text = message,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.primary
    )
}
