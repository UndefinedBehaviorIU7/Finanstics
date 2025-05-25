package com.ub.finanstics.presentation.addAction

import android.content.Context
import android.util.Log
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.db.Category
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager

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

    suspend fun getCategoriesNames(type: Int): List<com.ub.finanstics.api.models.Category>? {
        val apiRep = ApiRepository()
        var categories: MutableList<com.ub.finanstics.api.models.Category>? = null
        val preferencesManager = PreferencesManager(context)
        val groupId = preferencesManager.getInt("groupId", -1)

        try {
            val response = apiRep.getGroupCategories(groupId)
            if (response.isSuccessful) {
                val allCategories = response.body()
                if (allCategories != null) {
                    for (el in allCategories) {
                        if (el.type == type) {
                            if (categories == null) categories = mutableListOf()
                            categories.add(el)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("addActionGroupApi ERROR", e.toString())
        }

        return categories
    }


    @Suppress("MagicNumber", "LongParameterList", "LongMethod", "TooGenericExceptionCaught")
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

        Log.d("duplicationqqqq", duplication.toString())

        if (userId < 0 || token == "-1") {
            res = ErrorAddActionGroupApi.Error
        } else {
            try {
                val response = apiRep.addAction(
                    userId = userId,
                    token = token,
                    actionName = actionName,
                    type = if (type == 2) 1 else type,
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
