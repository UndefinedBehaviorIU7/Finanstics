package com.example.finanstics.presentation.stats

import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.ui.theme.EXPENSES_DATA_MONTHS
import com.example.finanstics.ui.theme.INCOMES_DATA_MONTHS

class StatsRepository {
    suspend fun getIncomes(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return INCOMES_DATA_MONTHS[month.number % 2].sortedByDescending { it.second }
    }

    suspend fun getExpenses(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return EXPENSES_DATA_MONTHS[month.number % 2].sortedByDescending { it.second }
    }
}
