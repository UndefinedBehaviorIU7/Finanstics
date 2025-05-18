package com.example.finanstics.presentation.addAction

import android.content.Context
import android.util.Log
import com.example.finanstics.api.ApiRepository
import com.example.finanstics.api.models.Group
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.calendar.ActionDataClass
import com.example.finanstics.presentation.preferencesManager.PreferencesManager

class AddActionRepository(private var db: FinansticsDatabase, private val context: Context) {
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    suspend fun getCategoriesNames(): List<String> {
        val categories = categoryDao.getAllCategories()
        return categories.map { it.name }
    }

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
}
