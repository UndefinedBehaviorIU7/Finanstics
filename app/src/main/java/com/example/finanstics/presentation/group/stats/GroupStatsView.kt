package com.example.finanstics.presentation.group.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.presentation.stats.DetailsPieChart
import com.example.finanstics.presentation.stats.PieChart

@Suppress("MagicNumber")
@Composable
fun GroupStats(
    navController: NavController,
    vm: GroupStatsViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        vm.fetchData()
    }

    val uiState = vm.uiState.collectAsState().value

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
            when (uiState) {
                is GroupStatsUiState.Done -> GroupStatsView(
                    uiState.incomes,
                    uiState.expenses,
                    uiState.month
                )

                is GroupStatsUiState.Error -> GroupStatsErrorView(uiState.message)
                GroupStatsUiState.Loading -> {}
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupStatsView(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>,
    month: Int
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = month.toString())
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
            item {
                Divider(
                    space = 10.dp,
                    stroke = 2.dp
                )
            }
            item {
                DetailsPieChart(
                    data = incomes,
                    expenses = false
                )
            }
            item {
                Divider(
                    space = 10.dp,
                    stroke = 2.dp
                )
            }
            item {
                DetailsPieChart(
                    data = expenses,
                    expenses = true
                )
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

@Suppress("MagicNumber")
@Composable
fun Divider(
    space: Dp,
    stroke: Dp
) {
    Spacer(modifier = Modifier.height(space))
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = stroke,
        color = MaterialTheme.colorScheme.secondary
    )
    Spacer(modifier = Modifier.height(10.dp))
}
