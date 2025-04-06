package com.example.finanstics.presentation.group.stats

import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.ui.theme.EXPENSES_DATA_MONTHS
import com.example.finanstics.ui.theme.INCOMES_DATA_MONTHS

class GroupStatsRepository {
    suspend fun getIncomes(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return INCOMES_DATA_MONTHS[month.number % INCOMES_DATA_MONTHS.size]
            .sortedByDescending { it.second }
    }

    suspend fun getExpenses(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return EXPENSES_DATA_MONTHS[month.number % EXPENSES_DATA_MONTHS.size]
            .sortedByDescending { it.second }
    }

    suspend fun getAllIncomes(): List<Pair<String, Int>> {
        return (INCOMES_DATA_MONTHS[0] + INCOMES_DATA_MONTHS[1] + INCOMES_DATA_MONTHS[3])
            .sortedByDescending { it.second }
    }

    suspend fun getAllExpenses(): List<Pair<String, Int>> {
        return (EXPENSES_DATA_MONTHS[0] + EXPENSES_DATA_MONTHS[1] + EXPENSES_DATA_MONTHS[3])
            .sortedByDescending { it.second }
    }
}
