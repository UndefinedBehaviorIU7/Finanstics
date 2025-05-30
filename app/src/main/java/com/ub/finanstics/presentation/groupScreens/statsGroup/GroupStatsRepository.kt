package com.ub.finanstics.presentation.groupScreens.statsGroup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.ub.finanstics.presentation.userScreens.calendar.MonthNameClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

fun sumPairs(list: List<Pair<String, Int>>): List<Pair<String, Int>> {
    return list.groupBy { it.first }
        .mapValues { (_, pairs) -> pairs.sumOf { it.second } }
        .toList()
        .sortedByDescending { it.second }
}

class GroupStatsRepository(private val context: Context) {
    private val preferencesManager = PreferencesManager(context)
    private val api = ApiRepository()

    @Suppress("TooGenericExceptionCaught")
    suspend fun getIncomes(
        month: MonthNameClass,
        year: Int
    ): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        var incomes: List<Pair<String, Int>>? = null
        try {
            val respAct = api.getGroupActionsByDate(
                groupId = groupId,
                year = year,
                month = month.number
            )
            val respCat = api.getGroupCategories(groupId)

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
        } catch (_: Exception) {
        }
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

        var expenses: List<Pair<String, Int>>? = null
        try {
            val respAct = api.getGroupActionsByDate(
                groupId = groupId,
                year = year,
                month = month.number
            )
            val respCat = api.getGroupCategories(groupId)

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
        } catch (_: Exception) {
        }
        return expenses
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getAllIncomes(): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        var incomes: List<Pair<String, Int>>? = null
        try {
            val respAct = api.getGroupActions(groupId)
            val respCat = api.getGroupCategories(groupId)

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
        } catch (_: Exception) {
        }
        return if (incomes != null) sumPairs(incomes) else null
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getAllExpenses(): List<Pair<String, Int>>? {
        val groupId = preferencesManager.getInt("groupId", -1)
        val userId = preferencesManager.getInt("id", -1)
        if (userId < 0 || groupId < 0) return null

        var expenses: List<Pair<String, Int>>? = null
        try {
            val respAct = api.getGroupActions(groupId)
            val respCat = api.getGroupCategories(groupId)

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
        } catch (_: Exception) {
        }
        return if (expenses != null) sumPairs(expenses) else null
    }

    fun balance(
        incomes: List<Pair<String, Int>>?,
        expenses: List<Pair<String, Int>>?
    ): Int? {
        if (incomes == null && expenses == null) return null
        return (incomes ?: emptyList()).sumOf { it.second } - (expenses
            ?: emptyList()).sumOf { it.second }
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

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    suspend fun groupImage(groupId: Int): Bitmap? {
        return try {
            val response = api.getGroupImage(groupId)
            if (response.isSuccessful) {
                response.body()?.byteStream().use { stream ->
                    if (stream != null) {
                        BitmapFactory.decodeStream(stream)
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getGroupImage(groupId: Int): Bitmap? = coroutineScope {
        val bitmapDeferred = async(Dispatchers.IO) { groupImage(groupId) }
        bitmapDeferred.await()
    }
}
