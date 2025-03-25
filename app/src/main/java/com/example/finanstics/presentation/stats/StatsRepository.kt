package com.example.finanstics.presentation.stats

import com.example.finanstics.ui.theme.EXPENSES_DATA
import com.example.finanstics.ui.theme.INCOMES_DATA

class StatsRepository() {
    suspend fun getIncomes(): List<Pair<String, Int>> {
        return INCOMES_DATA
    }

    suspend fun getExpenses(): List<Pair<String, Int>> {
        return EXPENSES_DATA
    }
}
