package com.ub.finanstics.presentation.addAction

import android.content.Context
import android.util.Log
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager

enum class ErrorAddActionApi(val str: String) {
    ERROR("ошибка сервера"),
    ERROR_USER_ID("вы не авторизированны"),
    ERROR_USER_TOKEN("ошибка сессии"),
    ERROR_ADD_ACTION_API("ошибка добавление действия на сервер"),
    Ok("ок")
}

class AddActionRepository(private var db: FinansticsDatabase, private val context: Context) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(type: Int): List<String> {
        val categories = if (type == 2)
            categoryDao.getIncomesCategories()
        else 
            categoryDao.getExpensesCategories()
        return categories.map { it.name }
    }

    @Suppress(
        "MagicNumber",
        "LongParameterList",
        "LongMethod",
        "ComplexMethod",
        "TooGenericExceptionCaught",
        "NestedBlockDepth"
    )
    suspend fun getUserGroup(): List<Group>? {
        val apiRep = ApiRepository()
        var resF: List<Group>?
        val preferencesManager = PreferencesManager(context)
        val userId = preferencesManager.getInt("id", -1)
        if (userId == -1)
            resF = null
        else {
            try {
                val response = apiRep.getUserGroups(
                    userId = userId,
                )
                if (!response.isSuccessful) {
                    resF = null
                } else {
                    val groups = response.body()
                    val res = mutableListOf<Group>()
                    if (groups != null) {
                        for (el in groups) {
                            res.add(el)
                        }
                    }
                    resF = res
                }
            } catch (e: Exception) {
                Log.e("getGroupActionDays ERROR", e.toString())
                resF = null
            }
        }
        return resF
    }

    @Suppress(
        "MagicNumber",
        "LongParameterList",
        "LongMethod",
        "ComplexMethod",
        "TooGenericExceptionCaught"
    )
    suspend fun addActionApi(
        actionName: String,
        type: Int,
        value: Int,
        date: String,
        categoryId: Int,
        description: String?,
        groups: List<Group>
    ): ErrorAddActionApi {
        Log.d("addActionApi", "enterF")

        var res = ErrorAddActionApi.Ok

        val apiRep = ApiRepository()

        val preferencesManager = PreferencesManager(context)
        val userId = preferencesManager.getInt("id", -1)
        val encryptedPrefManager = EncryptedPreferencesManager(context)
        val token = encryptedPrefManager.getString("token", "-1")

        if (userId < 0 || token == "-1") {
            res = if (userId == -1)
                ErrorAddActionApi.ERROR_USER_ID
            else
                ErrorAddActionApi.ERROR_USER_TOKEN
        } else {
            try {
                Log.d("addActionApi", "enter apiGo")
                val response = apiRep.addAction(
                    userId = userId,
                    token = token,
                    actionName = actionName,
                    type = type,
                    value = value,
                    date = date,
                    categoryId = categoryId,
                    description = description,
                    groupId = groups.map { it.id } + listOf(0)
                )
                if (!response.isSuccessful) {
                    res = ErrorAddActionApi.ERROR_ADD_ACTION_API
                }
            } catch (e: Exception) {
                res = ErrorAddActionApi.ERROR
            }
        }
        return res
    }
}
