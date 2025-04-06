package com.example.finanstics.presentation.group.stats

import com.example.finanstics.presentation.calendar.MonthNameClass
import com.example.finanstics.ui.theme.EXPENSES_DATA_MONTHS
import com.example.finanstics.ui.theme.INCOMES_DATA_MONTHS

fun sumPairs(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
    return list.groupBy { it.first }
        .mapValues { (_, pairs) -> pairs.sumOf { it.second } }
        .toList()
        .sortedByDescending { it.second }
}

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
        return sumPairs(INCOMES_DATA_MONTHS.flatten())
    }

    suspend fun getAllExpenses(): List<Pair<String, Int>> {
        return sumPairs(EXPENSES_DATA_MONTHS.flatten())
    }

    fun balance(
        incomes: List<Pair<String, Int>>,
        expenses: List<Pair<String, Int>>
    ): Int {
        return incomes.sumOf { it.second } - expenses.sumOf { it.second }
    }
}
