package com.example.finanstics.presentation.stats

import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.presentation.group.stats.sumPairs
import com.example.finanstics.ui.theme.EXPENSES_DATA_MONTHS
import com.example.finanstics.ui.theme.INCOMES_DATA_MONTHS

class StatsRepository {
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

    fun getAllIncomes(): List<Pair<String, Int>> {
        return sumPairs(INCOMES_DATA_MONTHS.flatten())
    }

    fun getAllExpenses(): List<Pair<String, Int>> {
        return sumPairs(EXPENSES_DATA_MONTHS.flatten())
    }

    fun balance(
        incomes: List<Pair<String, Int>>,
        expenses: List<Pair<String, Int>>
    ): Int {
        return incomes.sumOf{ it.second } - expenses.sumOf { it.second }
    }
}
