package com.example.finanstics.presentation.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finanstics.ui.theme.EXPENSES_DATA
import com.example.finanstics.ui.theme.INCOMES_DATA

@Suppress("MagicNumber")
@Preview
@Composable
fun PieChartPreview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        item {
            Row() {
                Column(modifier = Modifier.weight(1f)) {
                    PieChart(
                        data = INCOMES_DATA,
                        radiusOuter = 90.dp,
                        expenses = false,
                        chartBarWidth = 26.dp,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    PieChart(
                        data = EXPENSES_DATA,
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
                data = INCOMES_DATA,
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
                data = EXPENSES_DATA,
                expenses = true
            )
        }
    }
}
