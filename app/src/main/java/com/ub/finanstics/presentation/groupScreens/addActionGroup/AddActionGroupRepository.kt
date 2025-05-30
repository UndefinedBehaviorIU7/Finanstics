package com.ub.finanstics.presentation.groupScreens.addActionGroup

import android.content.Context
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.presentation.userScreens.addAction.ErrorAddAction
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager

@Suppress("TooGenericExceptionCaught", "NestedBlockDepth", "ComplexCondition")
class AddActionGroupRepository(private val context: Context) {
    private fun getCategoriesListByType(
        allCategories: List<Category>,
        type: Int
    ): MutableList<Category>? {
        val res: MutableList<Category> = mutableListOf()
        for (el in allCategories) {
            if (el.type == 1 || (el.type == type) || (el.type == 2 && type == 1)) {
                res.add(el)
            }
        }
        return if (res.isEmpty())
            null
        else
            res
    }

    suspend fun getCategoriesByType(type: Int): List<Category>? {
        val apiRep = ApiRepository()
        var res: MutableList<Category>? = null
        val preferencesManager = PreferencesManager(context)
        val groupId = preferencesManager.getInt("groupId", -1)
        try {
            val response = apiRep.getGroupCategories(groupId)
            if (response.isSuccessful) {
                val allCategories = response.body()
                if (allCategories != null)
                    res = getCategoriesListByType(allCategories, type)
            }
        } catch (_: Exception) {
            res = null
        }
        return res
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
    ): ErrorAddAction {
        var res = ErrorAddAction.OK

        val apiRep = ApiRepository()

        val preferencesManager = PreferencesManager(context)
        val encryptedPrefManager = EncryptedPreferencesManager(context)

        val userId = preferencesManager.getInt("id", -1)

        val groupId = preferencesManager.getInt("groupId", -1)
        val token = encryptedPrefManager.getString("token", "-1")

        if (userId < 0 || token == "-1") {
            res = ErrorAddAction.ERROR_ADD_DATA_SERVER
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
                    res = ErrorAddAction.ERROR_ADD_DATA_SERVER
                }
            } catch (_: Exception) {
                res = ErrorAddAction.ERROR_ADD_DATA_SERVER
            }
        }
        return res
    }
}
