package com.example.finanstics.presentation.stats

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Suppress("MagicNumber")
@Composable
fun Stats(
    vm: StatsViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        vm.fetchData()
    }

    val uiState = vm.uiState.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is StatsUiState.Done -> StatsView(
                uiState.incomes,
                uiState.expenses
            )

            is StatsUiState.Error -> StatsErrorView(uiState.message)
            StatsUiState.Loading -> TODO()
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun StatsView(
    incomes: List<Pair<String, Int>>,
    expenses: List<Pair<String, Int>>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
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
        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            DetailsPieChart(
                data = incomes,
                expenses = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            DetailsPieChart(
                data = expenses,
                expenses = true
            )
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
        color = Color.Black
    )
}
