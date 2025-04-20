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
            if (category == null) {
                Log.e("Sync", "No ${action.categoryId} in db")
            } else {
                if (category.serverId == null) {
                    syncLocalWithServerCategories(application)
                } else {
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
                        val serverResponse = response.body()
                        if (serverResponse != null) {
                            actionDao.updateServerId(action.actionId, serverResponse.id)
                        }
                    } else {
                        Log.e(
                            "Sync",
                            "Failed to sync action ${action.actionId}: " +
                                "${response.errorBody()?.string()}"
                        )
                    }
                }
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
    println(categoryDao.getAllCategories())
    val unsyncedCategories = categoryDao.getUnsyncedCategories()
    println("Unsynced categories:")
    println(unsyncedCategories)

    unsyncedCategories.forEach { category ->
        try {
            val response = api.addCategory(
                userId = USER_ID,
                token = TOKEN,
                categoryName = category.name,
                type = category.type
            )

            if (response.isSuccessful) {
                val serverResponse = response.body()
                if (serverResponse != null) {
                    categoryDao.updateServerId(category.id, serverResponse.id)
                }
            } else {
                Log.e(
                    "Sync",
                    "Failed to sync category ${category.id}: " +
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
    val categoryDao = db.categoryDao()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    try {
        val response = api.getUserActions(USER_ID)

        if (response.isSuccessful) {
            val serverActions = response.body()
            val localActions = db.actionDao().getAllActions()
            println(localActions)

            serverActions?.forEach { serverAction ->
                var new = true
                localActions.forEach { localAction ->
                    if (serverAction.id == localAction.serverId) {
                        println("Action ${serverAction.name} has already been downloaded")
                        new = false
                    }
                }
                if (new) {
                    println(serverAction)
                    Log.i(
                        "Sync",
                        "Load action ${serverAction.name} from server"
                    )
                    if (categoryDao.getCategoryByServerId(serverAction.category_id) == null) {
                        Log.e(
                            "Sync",
                            "No category ${serverAction.category_id} from server"
                        )
                    } else {
                        val newAction = Action(
                            name = serverAction.name,
                            type = serverAction.type,
                            description = serverAction.description,
                            value = serverAction.value,
                            date = LocalDate.parse(serverAction.date, formatter),
                            categoryId = serverAction.category_id,
                            serverId = serverAction.id
                        )
                        actionDao.insertAction(newAction)
                    }
                }
            }
            Log.i(
                "Sync",
                "Loaded actions from server to local db"
            )
        } else {
            Log.e(
                "Sync",
                "Failed to load actions from server to local db:" +
                    "${response.errorBody()?.string()}"
            )
        }
    } catch (e: Exception) {
        Log.e(
            "Sync",
            "Error syncing action from server to local",
            e
        )
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

            serverCategories?.forEach { serverCategory ->
                val cat = categoryDao
                    .getCategoryByNameAndType(
                        serverCategory.name,
                        serverCategory.type
                    )
                if (cat != null) {
                    categoryDao.updateServerId(
                        cat.id,
                        serverCategory.id
                    )
                    Log.i(
                        "Sync",
                        "Category ${cat.name} updated from server id"
                    )
                } else {
                    categoryDao.insertCategory(
                        Category(
                            name = serverCategory.name,
                            type = serverCategory.type,
                            serverId = serverCategory.id
                        )
                    )
                    Log.i(
                        "Sync",
                        "Category ${serverCategory.name} loaded from server"
                    )
                }
            }
            Log.i(
                "Sync",
                "Loaded categories from server to local db is done"
            )
        } else {
            Log.e(
                "Sync",
                "Failed to load actions from server to local db:" +
                    "${response.errorBody()?.string()}"
            )
        }
    } catch (e: Exception) {
        Log.e(
            "Sync",
            "Error syncing action from server to local",
            e
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncData(application: Application) {
    syncLocalWithServerCategories(application)
    syncServerWithLocalCategories(application)

    syncServerWithLocalActions(application)
    syncLocalWithServerActions(application)
}
