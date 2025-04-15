package com.example.finanstics.presentation.stats

import com.example.finanstics.db.Action
import com.example.finanstics.db.Category
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.calendar.MonthNameClass

class StatsRepository(private var db: FinansticsDatabase) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getIncomes(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getIncomesByMonth(month, year), getAllCategories())
    }

    suspend fun getExpenses(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getExpensesByMonth(month, year), getAllCategories())
    }

    suspend fun getAllIncomes(): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getAllIncomes(), getAllCategories())
            .sortedByDescending { it.second }
    }

    suspend fun getAllExpenses(): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getAllExpenses(), getAllCategories())
            .sortedByDescending { it.second }
    }

    suspend fun getAllActions(): List<Action> {
        return actionDao.getAllActions()
    }

    suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories()
    }

    fun balance(
        incomes: List<Pair<String, Int>>,
        expenses: List<Pair<String, Int>>
    ): Int {
        return incomes.sumOf { it.second } - expenses.sumOf { it.second }
    }

    fun actionsToPairs(actions: List<Action>, categories: List<Category>): List<Pair<String, Int>> {
        val categoryMap = categories.associateBy { it.id }

        return actions
            .map { action ->
                val name = action.categoryId
                    .let { categoryMap[it]!!.name }
                action.value.let { name to it }
            }
    }
}
