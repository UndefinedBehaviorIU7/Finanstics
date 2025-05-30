package com.ub.finanstics.presentation.userScreens.addAction

import android.content.Context
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager

class AddActionRepository(private var db: FinansticsDatabase, private val context: Context) {
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(type: Int): List<String> {
        val categories = if (type == 1)
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
        "NestedBlockDepth",
        "ComplexCondition"
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
        "TooGenericExceptionCaught",
        "NestedBlockDepth"
    )
    suspend fun addActionApi(
        actionName: String,
        type: Int,
        value: Int,
        date: String,
        category: String,
        description: String?,
        groups: List<Group>
    ): ErrorAddAction {
        var res = ErrorAddAction.OK

        val apiRep = ApiRepository()

        val preferencesManager = PreferencesManager(context)
        val userId = preferencesManager.getInt("id", -1)
        val encryptedPrefManager = EncryptedPreferencesManager(context)
        val token = encryptedPrefManager.getString("token", "-1")

        if (userId < 0 || token == "-1") {
            res = if (userId == -1)
                ErrorAddAction.ERROR_ADD_DATA_SERVER
            else
                ErrorAddAction.ERROR_ADD_DATA_SERVER
        } else {
            try {
                val categoryDao = db.categoryDao()
                val categoryId = categoryDao.getCategoryByName(category)
                if (categoryId != null) {
                    val response = apiRep.addAction(
                        userId = userId,
                        token = token,
                        actionName = actionName,
                        type = type,
                        value = value,
                        date = date,
                        categoryId = categoryId.id,
                        description = description,
                        groupId = groups.map { it.id } + listOf(0)
                    )
                    if (!response.isSuccessful) {
                        res = ErrorAddAction.ERROR_ADD_DATA_SERVER
                    }
                }
            } catch (e: Exception) {
                res = ErrorAddAction.ERROR_ADD_DATA_SERVER
            }
        }
        return res
    }
}
