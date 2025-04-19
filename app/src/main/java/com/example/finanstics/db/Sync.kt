package com.example.finanstics.db

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.ui.theme.TOKEN
import com.example.finanstics.ui.theme.USER_ID
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncLocalWithServerActions(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val actionDao = db.actionDao()
    val categoryDao = db.categoryDao()

    val unsyncedActions = actionDao.getUnsyncedActions()
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    unsyncedActions.forEach { action ->
        try {
            val category = categoryDao.getCategoryById(action.categoryId)
            if (category?.serverId != null) {
                val response = api.addAction(
                    userId = USER_ID,
                    token = TOKEN,
                    actionName = action.name,
                    actionType = action.type,
                    value = action.value,
                    date = action.date.format(dateFormatter),
                    categoryId = category.serverId!!,
                    description = action.description,
                    groupId = null
                )

                if (response.isSuccessful) {
                    val serverId = response.body()
                    if (serverId != null) {
                        actionDao.updateServerId(action.actionId, serverId.id)
                    }
                } else {
                    Log.e(
                        "Sync", "Failed to sync action ${action.actionId}: " +
                                "${response.errorBody()?.string()}"
                    )
                }
            } else {
                Log.e(
                    "Sync", "Failed to sync action. There is not such category"
                )
            }

        } catch (e: Exception) {
            Log.e("Sync", "Error syncing action ${action.actionId}", e)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncLocalWithServerCategories(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val categoryDao = db.categoryDao()
    val unsyncedCategories = categoryDao.getUnsyncedCategories()

    unsyncedCategories.forEach { category ->
        try {
            val response = api.addCategory(
                userId = USER_ID,
                token = TOKEN,
                categoryName = category.name,
                type = category.type
            )

            if (response.isSuccessful) {
                val serverId = response.body()
                if (serverId != null) {
                    categoryDao.updateServerId(category.id, serverId.id)
                }
            } else {
                Log.e(
                    "Sync", "Failed to sync action ${category.id}: " +
                            "${response.errorBody()?.string()}"
                )
            }
        } catch (e: Exception) {
            Log.e("Sync", "Error syncing action ${category.id}", e)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerWithLocalActions(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val actionDao = db.actionDao()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    try {
        val response = api.getUserActions(USER_ID)

        if (response.isSuccessful) {
            val serverActions = response.body()
            val localActions = db.actionDao().getAllActions()

            serverActions?.forEach { serverAction ->
                var new = true
                localActions.forEach { localAction ->
                    if (serverAction.id == localAction.serverId)
                        new = false
                }
                if (new) {
                    Log.e(
                        "Sync", "NEW"
                    )
                    val newAction = Action(
                        name = serverAction.name,
                        type = serverAction.type,
                        description = serverAction.description,
                        value = serverAction.value,
                        date = LocalDate.parse(serverAction.date, formatter),
                        categoryId = serverAction.categoryId,
                        serverId = serverAction.id
                    )

                    val categoryExists = db.categoryDao()
                        .getCategoryById(serverAction.categoryId) != null
                    if (categoryExists) {
                        actionDao.insertAction(newAction)
                    } else {
                        Log.e(
                            "SyncError", "Category ${serverAction.categoryId} " +
                                    "not found for action ${serverAction.name}"
                        )
                    }
                }
            }
            Log.i(
                "Sync", "Loaded actions from server to local db"
            )
        } else {
            Log.e(
                "Sync", "Failed to load actions from server to local db"
            )
        }
    } catch (e: Exception) {
        Log.e("Sync", "Error syncing action from server to local", e)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerWithLocalCategories(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val categoryDao = db.categoryDao()

    try {
        val response = api.getUserCategories(USER_ID)

        if (response.isSuccessful) {
            val serverCategories = response.body()
            val localCategories = categoryDao.getAllCategories()

            serverCategories?.forEach { serverCategory ->
                var new = true
                localCategories.forEach { localCategory ->
                    if (serverCategory.id == localCategory.serverId)
                        new = false
                }
                if (new) {
                    val newCategory = Category(
                        name = serverCategory.name,
                        type = serverCategory.type,
                    )
                    categoryDao.insertCategory(newCategory)
                }
            }
            Log.i(
                "Sync", "Loaded categories from server to local db"
            )
        } else {
            Log.e(
                "Sync", "Failed to load actions from server to local db"
            )
        }
    } catch (e: Exception) {
        Log.e("Sync", "Error syncing action from server to local", e)
    }
}
