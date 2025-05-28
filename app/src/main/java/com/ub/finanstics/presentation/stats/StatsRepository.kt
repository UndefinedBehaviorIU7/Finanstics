package com.ub.finanstics.presentation.stats

import android.content.Context
import com.ub.finanstics.R
import com.ub.finanstics.db.Action
import com.ub.finanstics.db.Category
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.MonthNameClass

class StatsRepository(private val context: Context) {
    val db = FinansticsDatabase.getDatabase(context)
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

    suspend fun initCategories() {
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.food),
                type = 0
            )
        )
        categoryDao.insertCategory(Category(name = context.getString(R.string.transport), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.taxes), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.shopping), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.sport), type = 0))
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.entertainments),
                type = 0
            )
        )
        categoryDao.insertCategory(Category(name = context.getString(R.string.education), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.self_care), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.health), type = 0))
        categoryDao.insertCategory(Category(name = context.getString(R.string.life), type = 0))
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.other_expenses),
                type = 0
            )
        )
        categoryDao.insertCategory(Category(name = context.getString(R.string.salary), type = 2))
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.money_transfer),
                type = 1
            )
        )
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.scholarship),
                type = 2
            )
        )
        categoryDao.insertCategory(Category(name = context.getString(R.string.pension), type = 2))
        categoryDao.insertCategory(Category(name = context.getString(R.string.percents), type = 2))
        categoryDao.insertCategory(
            Category(
                name = context.getString(R.string.other_incomes),
                type = 2
            )
        )
    }
}
