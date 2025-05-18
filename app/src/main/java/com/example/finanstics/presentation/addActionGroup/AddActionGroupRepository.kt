package com.example.finanstics.presentation.addAction

import android.content.Context
import android.util.Log
import com.example.finanstics.api.ApiRepository
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.preferencesManager.PreferencesManager

enum class ErrorAddActionGroupApi(val str: String) {
    Error("ошибка сервера"),
    Ok("ок")
}

//@Suppress("TooGenericExceptionCaught")
//suspend fun getCategoriesById(
//    userId: Int
//): String? {
//    var res: String? = null
//    try {
//        val apiRep = ApiRepository()
//        val response = apiRep.getUserCategories(userId)
//        if (!response.isSuccessful) {
//            Log.e("getUserName", "not isSuccessful")
//        } else {
//            res = response.body()?.username
//        }
//    } catch (e: Exception) {
//        Log.e("getUserName", e.toString())
//    }
//    return res
//}


class AddActionGroupRepository(private var db: FinansticsDatabase, private val context: Context) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(): List<String> {
        val categories = categoryDao.getAllCategories()
        return categories.map { it.name }
    }

    suspend fun AddActionGroupApi(
        actionName: String,
        type: Int,
        value: Int,
        date: String,
        category: String,
        description: String?,
    ): ErrorAddActionGroupApi {
        val apiRep = ApiRepository()
        var res = ErrorAddActionGroupApi.Ok
        val preferencesManager = PreferencesManager(context)
        val userId = preferencesManager.getInt("id", -1)
        val token = preferencesManager.getString("token", "-1")
        if (userId == -1 || token == "-1")
            res = ErrorAddActionGroupApi.Error
        else {
            try {
                val response = apiRep.addAction(
                    userId = userId,
                    token = token,
                    actionName = actionName,
                    type = type,
                    value = value,
                    date = date,
                    categoryId = 1,
                    description = description,
                    groupId = listOf(2)
                )
                if (!response.isSuccessful) {
                    res = ErrorAddActionGroupApi.Error
                }
            } catch (e: Exception) {
                Log.e("getGroupActionDays ERROR", e.toString())
                res = ErrorAddActionGroupApi.Error
            }
        }
        return res
    }
}
