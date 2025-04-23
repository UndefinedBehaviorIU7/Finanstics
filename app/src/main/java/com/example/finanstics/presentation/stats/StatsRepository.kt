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
        return actionsToPairs(
            actionDao.getIncomesByMonthYear(month.number, year),
            getAllCategories()
        )
            .groupBy { it.first }
            .map { (category, pairs) ->
                category to pairs.sumOf { it.second }
            }
            .sortedByDescending { it.second }
    }

    suspend fun getExpenses(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>> {
        return actionsToPairs(
            actionDao.getExpensesByMonthYear(month.number, year),
            getAllCategories()
        )
            .groupBy { it.first }
            .map { (category, pairs) ->
                category to pairs.sumOf { it.second }
            }
            .sortedByDescending { it.second }
    }

    suspend fun getAllIncomes(): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getAllIncomes(), getAllCategories())
            .groupBy { it.first }
            .map { (category, pairs) ->
                category to pairs.sumOf { it.second }
            }
            .sortedByDescending { it.second }
    }

    suspend fun getAllExpenses(): List<Pair<String, Int>> {
        return actionsToPairs(actionDao.getAllExpenses(), getAllCategories())
            .groupBy { it.first }
            .map { (category, pairs) ->
                category to pairs.sumOf { it.second }
            }
            .sortedByDescending { it.second }
    }

    suspend fun getAllActions(): List<Triple<String, Int, Int>> {
        return actionsToTriples(actionDao.getAllActions(), categoryDao.getAllCategories())
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

    fun actionsToTriples(actions: List<Action>, categories: List<Category>):
            List<Triple<String, Int, Int>> {
        val categoryMap = categories.associateBy { it.id }

        return actions.map { action ->
            val name = categoryMap[action.categoryId]?.name ?: "Unknown"
            Triple(name, action.value, action.type)
        }
    }
}
