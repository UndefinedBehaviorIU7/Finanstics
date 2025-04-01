package com.example.finanstics.presentation.group.stats

import com.example.finanstics.ui.theme.EXPENSES_DATA
import com.example.finanstics.ui.theme.INCOMES_DATA

class GroupStatsRepository {
    suspend fun getIncomes(): List<Pair<String, Int>> {
        return INCOMES_DATA
    }

    suspend fun getExpenses(): List<Pair<String, Int>> {
        return EXPENSES_DATA
    }
}
