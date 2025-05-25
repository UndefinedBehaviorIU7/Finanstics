package com.ub.finanstics.presentation.statsGroup

import android.content.Context
import android.util.Log
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.presentation.calendar.MonthNameClass
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager

fun sumPairs(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
    return list.groupBy { it.first }
        .mapValues { (_, pairs) -> pairs.sumOf { it.second } }
        .toList()
        .sortedByDescending { it.second }
}

class GroupStatsRepository(private val context: Context) {
    val preferencesManager = PreferencesManager(context)

    @Suppress("TooGenericExceptionCaught")
    suspend fun getIncomes(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        val apiRep = ApiRepository()
        var incomes: List<Pair<String, Int>>? = null
        try {
            val respAct = apiRep.getGroupActionsByDate(
                groupId = groupId,
                year = year,
                month = month.number
            )
            val respCat = apiRep.getGroupCategories(groupId)

            if (respAct.isSuccessful && respCat.isSuccessful) {
                val actions = respAct.body()
                val categories = respCat.body()
                Log.d("DATA", "$categories")

                if (actions != null && categories != null) {
                    incomes = actionsToPairs(
                        actions.filter { it.type == 1 },
                        categories
                    )
                        .groupBy { it.first }
                        .map { (category, pairs) ->
                            category to pairs.sumOf { it.second }
                        }
                        .sortedByDescending { it.second }
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActions ERROR", e.toString())
        }
        Log.d("DATA", "$incomes")
        return incomes
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getExpenses(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        val apiRep = ApiRepository()
        var expenses: List<Pair<String, Int>>? = null
        try {
            val respAct = apiRep.getGroupActionsByDate(
                groupId = groupId,
                year = year,
                month = month.number
            )
            val respCat = apiRep.getGroupCategories(groupId)

            if (respAct.isSuccessful && respCat.isSuccessful) {
                val actions = respAct.body()
                val categories = respCat.body()
                Log.d("DATA", "$categories")

                if (actions != null && categories != null) {
                    expenses = actionsToPairs(
                        actions.filter { it.type == 0 },
                        categories
                    )
                        .groupBy { it.first }
                        .map { (category, pairs) ->
                            category to pairs.sumOf { it.second }
                        }
                        .sortedByDescending { it.second }
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActions ERROR", "$e")
        }
        return expenses
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getAllIncomes(): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        val apiRep = ApiRepository()
        var incomes: List<Pair<String, Int>>? = null
        try {
            val respAct = apiRep.getGroupActions(groupId)
            val respCat = apiRep.getGroupCategories(groupId)

            if (respAct.isSuccessful && respCat.isSuccessful) {
                val actions = respAct.body()
                val categories = respCat.body()

                if (actions != null && categories != null) {
                    incomes = actionsToPairs(
                        actions.filter { it.type == 1 },
                        categories
                    )
                        .groupBy { it.first }
                        .map { (category, pairs) ->
                            category to pairs.sumOf { it.second }
                        }
                        .sortedByDescending { it.second }
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActions ERROR", e.toString())
        }
        return if (incomes != null) sumPairs(incomes) else null
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getAllExpenses(): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        val apiRep = ApiRepository()
        var expenses: List<Pair<String, Int>>? = null
        try {
            val respAct = apiRep.getGroupActions(groupId)
            val respCat = apiRep.getGroupCategories(groupId)

            if (respAct.isSuccessful && respCat.isSuccessful) {
                val actions = respAct.body()
                val categories = respCat.body()

                if (actions != null && categories != null) {
                    expenses = actionsToPairs(
                        actions.filter { it.type == 0 },
                        categories
                    )
                        .groupBy { it.first }
                        .map { (category, pairs) ->
                            category to pairs.sumOf { it.second }
                        }
                        .sortedByDescending { it.second }
                }
            }
        } catch (e: Exception) {
            Log.e("getGroupActions ERROR", e.toString())
        }
        return if (expenses != null) sumPairs(expenses) else null
    }

    fun balance(
        incomes: List<Pair<String, Int>>?,
        expenses: List<Pair<String, Int>>?
    ): Int? {
        if (incomes == null && expenses == null) return null
        return incomes!!.sumOf { it.second } - expenses!!.sumOf { it.second }
    }

    fun actionsToPairs(actions: List<Action>, categories: List<Category>): List<Pair<String, Int>> {
        val categoryMap = categories.associateBy { it.id }

        return actions
            .map { action ->
                val name = action.category_id
                    .let { categoryMap[it]!!.name }
                action.value.let { name to it }
            }
    }
}
