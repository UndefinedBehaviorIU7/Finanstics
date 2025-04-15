package com.example.finanstics.presentation.stats

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.ui.theme.USER_NAME

@Suppress("MagicNumber", "LongMethod")
@Composable
fun Stats(
    navController: NavController,
) {
    val vm: StatsViewModel = viewModel()
    val incomes = vm.incomes
    val expenses = vm.expenses

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(
                top = 40.dp,
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
                        Header(USER_NAME)
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                    }
                }

                is StatsUiState.LoadingData -> {
                    val calendar = uiState.calendar
                    Column() {
                        Header(USER_NAME)
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                        Loader(
                            modifier = Modifier
                        )
                    }
                }

                is StatsUiState.Done -> {
                    val calendar = uiState.calendar
                    Column(modifier = Modifier) {
                        Header(USER_NAME)
                        Spacer(modifier = Modifier.height(5.dp))
                        CalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                        StatsView(
                            incomes,
                            expenses,
                            vm
                        )
                    }
                }

                is StatsUiState.Error -> StatsErrorView(uiState.message)
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun StatsView(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel = viewModel()
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
                vm = vm
            )
        } else {
            StatsViewHorizontal(
                incomes = incomes,
                expenses = expenses,
                vm = vm
            )
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun StatsViewVertical(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    if (uiState is StatsUiState.Done) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        ) {
            item {
                Row() {
                    Column(modifier = Modifier.weight(1f)) {
                        PieChart(
                            data = incomes,
                            radiusOuter = 90.dp,
                            expenses = false,
                            chartBarWidth = 26.dp,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        PieChart(
                            data = expenses,
                            radiusOuter = 90.dp,
                            expenses = true,
                            chartBarWidth = 26.dp,
                        )
                    }
                }
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
                    data = incomes,
                    expenses = false
                )
            }
            item { Divider(10.dp, 2.dp) }
            item {
                DetailsPieChart(
                    data = expenses,
                    expenses = true
                )
            }
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }
}

@Suppress("MagicNumber", "LongMethod")
@Composable
fun StatsViewHorizontal(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: StatsViewModel
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
                            data = incomes,
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
                            data = expenses,
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

@Suppress("MagicNumber")
@Composable
fun Header(
    userName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = userName,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun Divider(
    space: Dp,
    stroke: Dp,
    after: Dp = 10.dp
) {
    Spacer(modifier = Modifier.height(space))
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = stroke,
        color = MaterialTheme.colorScheme.secondary
    )
    Spacer(modifier = Modifier.height(after))
}

@Composable
fun Loader(modifier: Modifier) {
    CircularProgressIndicator(modifier)
}
