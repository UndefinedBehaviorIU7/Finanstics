package com.example.finanstics.presentation.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.finanstics.ui.theme.EXPENSES_DATA
import com.example.finanstics.ui.theme.INCOMES_DATA

@Suppress("MagicNumber")
@Preview
@Composable
fun StatsPreview() {
    StatsView(
        incomes = INCOMES_DATA,
        expenses = EXPENSES_DATA
    )
}
