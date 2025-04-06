package com.example.finanstics.presentation.group.stats

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.finanstics.ui.theme.EXPENSES_DATA
import com.example.finanstics.ui.theme.FinansticsTheme
import com.example.finanstics.ui.theme.INCOMES_DATA

@Suppress("MagicNumber")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun StatsPreview() {
    FinansticsTheme(
        dynamicColor = false
    ) {
        GroupStatsView(
            incomes = INCOMES_DATA,
            expenses = EXPENSES_DATA
        )
    }
}
