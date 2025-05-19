package com.example.finanstics.presentation.addAction

import android.content.Context
import android.util.Log
import com.example.finanstics.api.ApiRepository
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.example.finanstics.presentation.preferencesManager.PreferencesManager

enum class ErrorAddActionGroupApi(val str: String) {
    Error("ошибка сервера"),
    Ok("ок")
}

class AddActionGroupRepository(
    private var db: FinansticsDatabase,
    private val context: Context
) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(): List<String> {
        val categories = categoryDao.getAllCategories()
        return categories.map { it.name }
    }

    @Suppress("MagicNumber",
        "LongParameterList", "LongMethod", "ComplexMethod", "TooGenericExceptionCaught")
    suspend fun addActionApi(
        actionName: String,
        type: Int,
        value: Int,
        date: String,
        categoryId: Int,
        description: String?,
        duplication: Boolean
    ): ErrorAddActionGroupApi {
        Log.d("addActionGroupApi", "startF")

        var res = ErrorAddActionGroupApi.Ok

        val apiRep = ApiRepository()

        val preferencesManager = PreferencesManager(context)
        val encryptedPrefManager = EncryptedPreferencesManager(context)

        val userId = preferencesManager.getInt("id", -1)

        val groupId = preferencesManager.getInt("groupId", -1)
        val token = encryptedPrefManager.getString("token", "-1")

        if (userId < 0 || token == "-1") {
            res = ErrorAddActionGroupApi.Error
        } else {
            try {
                val response = apiRep.addAction(
                    userId = userId,
                    token = token,
                    actionName = actionName,
                    type = type,
                    value = value,
                    date = date,
                    categoryId = categoryId,
                    description = description,
                    groupId = listOf(groupId) + if (duplication) listOf(0) else emptyList()
                )

                if (!response.isSuccessful) {
                    res = ErrorAddActionGroupApi.Error
                }

                Log.d("addActionGroupApi", "res: ${res.str}")
            } catch (e: Exception) {
                Log.e("addActionGroupApi ERROR", e.toString())
                res = ErrorAddActionGroupApi.Error
            }
        }
        return res
    }
}
