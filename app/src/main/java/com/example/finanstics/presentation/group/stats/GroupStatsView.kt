package com.example.finanstics.presentation.group.stats

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.presentation.stats.DetailsPieChart
import com.example.finanstics.presentation.stats.Divider
import com.example.finanstics.presentation.stats.Loader
import com.example.finanstics.presentation.stats.PieChart
import com.example.finanstics.ui.theme.ALL_TIME
import com.example.finanstics.ui.theme.GROUP_NAME

@Suppress("MagicNumber", "LongMethod")
@Composable
fun GroupStats(
    navController: NavController
) {
    val vm: GroupStatsViewModel = viewModel()

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
                GroupStatsUiState.Loading -> {
                    Loader(
                        modifier = Modifier
                    )
                }

                is GroupStatsUiState.Calendar -> {
                    val calendar = uiState.calendar
                    Column() {
                        Header(
                            GROUP_NAME,
                            uiState.all,
                            vm
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                    }
                }

                is GroupStatsUiState.LoadingData -> {
                    val calendar = uiState.calendar
                    Column() {
                        Header(
                            GROUP_NAME,
                            uiState.all,
                            vm
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                        Loader(
                            modifier = Modifier
                        )
                    }
                }

                is GroupStatsUiState.Done -> {
                    val calendar = uiState.calendar
                    Column(modifier = Modifier) {
                        Header(
                            GROUP_NAME,
                            uiState.all,
                            vm
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        GroupCalendarSwitch(
                            calendar = calendar,
                            vm = vm
                        )
                        GroupStatsView(
                            uiState.incomes,
                            uiState.expenses,
                            vm
                        )
                    }
                }

                is GroupStatsUiState.Error -> GroupStatsErrorView(uiState.message)
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun Header(
    groupName: String,
    isClicked: Boolean,
    vm: GroupStatsViewModel = viewModel()
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = groupName,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(0.2f))

        Button(
            onClick = {
                vm.switchAll()
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
                fontSize = 18.sp,
                text = "Всё время"
            )
        }
        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupStatsView(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: GroupStatsViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (!isLandscape) {
            GroupStatsViewVertical(
                incomes = incomes,
                expenses = expenses,
                vm = vm
            )
        } else {
            GroupStatsViewHorizontal(
                incomes = incomes,
                expenses = expenses,
                vm = vm
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupStatsViewVertical(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: GroupStatsViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    if (uiState is GroupStatsUiState.Done) {
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
                GroupBalance(
                    currentBalance = vm.balance(incomes, expenses),
                    period = if (uiState.all) ALL_TIME
                    else MonthNameClass.str(uiState.calendar.getData().getMonth()),
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

@Suppress("MagicNumber")
@Composable
fun GroupStatsViewHorizontal(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    vm: GroupStatsViewModel
) {
    val uiState = vm.uiState.collectAsState().value
    if (uiState is GroupStatsUiState.Done) {
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
                        GroupBalance(
                            currentBalance = vm.balance(incomes, expenses),
                            period = if (uiState.all) ALL_TIME
                            else MonthNameClass.str(uiState.calendar.getData().getMonth()),
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
