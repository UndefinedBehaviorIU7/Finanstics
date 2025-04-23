package com.example.finanstics.db

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import com.example.finanstics.ui.theme.TOKEN
import com.example.finanstics.ui.theme.USER_ID
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("TooGenericExceptionCaught")
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
                return@forEach
            }

            if (category.serverId == null) {
                syncLocalWithServerCategories(application)
                return@forEach
            }
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
            if (!response.isSuccessful) {
                Log.e(
                    "Sync",
                    "Failed to sync action ${action.actionId}: " +
                        "${response.errorBody()?.string()}"
                )
                return@forEach
            }
            val serverResp = response.body()
            if (serverResp != null) {
                actionDao.updateServerId(action.actionId, serverResp.id)
                actionDao.updateCreationTime(action.actionId, serverResp.created_at)
            }
        } catch (e: Exception) {
            Log.e("Sync", "Error syncing action ${action.actionId}", e)
        }
    }
}

@Suppress("TooGenericExceptionCaught")
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

            if (!response.isSuccessful) {
                Log.e(
                    "Sync",
                    "Failed to sync category ${category.id}: " +
                        "${response.errorBody()?.string()}"
                )
                return@forEach
            }
            val serverResp = response.body()
            if (serverResp != null) {
                categoryDao.updateServerId(category.id, serverResp.id)
                categoryDao.updateCreationTime(category.id, serverResp.created_at)
            }
        } catch (e: Exception) {
            Log.e("Sync", "Error syncing action ${category.id}", e)
        }
    }
}

@Suppress("LongMethod", "TooGenericExceptionCaught", "NestedBlockDepth")
@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerWithLocalActions(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val actionDao = db.actionDao()
    val categoryDao = db.categoryDao()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val preferencesManager = PreferencesManager(application)

    try {
        val response = api.getUserActionsSince(USER_ID, preferencesManager.getUpdateTime())
        if (!response.isSuccessful) {
            Log.e(
                "Sync",
                "Failed to load actions from server to local db:" +
                    "${response.errorBody()?.string()}"
            )
        }
        val serverActions = response.body()
        val localActions = db.actionDao().getAllActions()
        println(localActions)

        serverActions?.forEach { serverAction ->
            var new = true
            localActions.forEach { locAct -> if (serverAction.id == locAct.serverId) new = false }
            if (!new) return@forEach

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
                return@forEach
            }
            val newAction = Action(
                name = serverAction.name,
                type = serverAction.type,
                description = serverAction.description,
                value = serverAction.value,
                date = LocalDate.parse(serverAction.date, formatter),
                categoryId = categoryDao.getCategoryByServerId(serverAction.category_id)!!.id,
                serverId = serverAction.id,
                createdAt = serverAction.created_at
            )
            actionDao.insertAction(newAction)
            preferencesManager.saveUpdateTime()
        }
        Log.i(
            "Sync",
            "Loaded actions from server to local db"
        )
    } catch (e: Exception) {
        Log.e(
            "Sync",
            "Error syncing action from server to local",
            e
        )
    }
}

@Suppress("TooGenericExceptionCaught", "LongMethod")
@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncServerWithLocalCategories(application: Application) {
    val api = RetrofitInstance.api
    val db = FinansticsDatabase.getDatabase(application)
    val categoryDao = db.categoryDao()
    val preferencesManager = PreferencesManager(application)

    try {
        val response = api.getUserCategoriesSince(USER_ID, preferencesManager.getUpdateTime())
        if (!response.isSuccessful) {
            Log.e(
                "Sync",
                "Failed to load actions from server to local db:" +
                    "${response.errorBody()?.string()}"
            )
        }
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
                categoryDao.updateCreationTime(
                    cat.id,
                    serverCategory.createdAt!!
                )
                preferencesManager.saveUpdateTime()
                Log.i(
                    "Sync",
                    "Category ${cat.name} updated from server id"
                )
                return@forEach
            }
            categoryDao.insertCategory(
                Category(
                    name = serverCategory.name,
                    type = serverCategory.type,
                    serverId = serverCategory.id,
                    createdAt = serverCategory.createdAt
                )
            )
            preferencesManager.saveUpdateTime()
            Log.i(
                "Sync",
                "Category ${serverCategory.name} loaded from server"
            )
        }
        Log.i(
            "Sync",
            "Loaded categories from server to local db is done"
        )
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
