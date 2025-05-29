package com.ub.finanstics.db

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("TooGenericExceptionCaught")
@RequiresApi(Build.VERSION_CODES.O)
suspend fun syncLocalWithServerActions(application: Application) {
    val prefManager = PreferencesManager(application)
    val encryptedPrefManager = EncryptedPreferencesManager(application)
    val userId = prefManager.getInt("id", 0)
    val token = encryptedPrefManager.getString("token", "")

    if (token.isEmpty() || userId == 0) return
    val apiRep = ApiRepository()
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
            val response = apiRep.addAction(
                userId = userId,
                token = token,
                actionName = action.name,
                type = action.type,
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
    val prefManager = PreferencesManager(application)
    val encryptedPrefManager = EncryptedPreferencesManager(application)
    val userId = prefManager.getInt("id", 0)
    val token = encryptedPrefManager.getString("token", "")

    if (token.isEmpty() || userId == 0) return
    val apiRep = ApiRepository()
    val db = FinansticsDatabase.getDatabase(application)
    val categoryDao = db.categoryDao()
    val unsyncedCategories = categoryDao.getUnsyncedCategories()

    unsyncedCategories.forEach { category ->
        try {
            val response = apiRep.addCategory(
                userId = userId,
                token = token,
                categoryName = category.name,
                type = category.type
            )

            if (!response.isSuccessful) {
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
    val prefManager = PreferencesManager(application)
    val encryptedPrefManager = EncryptedPreferencesManager(application)
    val userId = prefManager.getInt("id", 0)
    val token = encryptedPrefManager.getString("token", "")

    if (token.isEmpty() || userId == 0) return
    val apiRep = ApiRepository()
    val db = FinansticsDatabase.getDatabase(application)
    val actionDao = db.actionDao()
    val categoryDao = db.categoryDao()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val preferencesManager = PreferencesManager(application)

    try {
        val response = apiRep.getUserActionsSince(userId, preferencesManager.getUpdateTime())
        if (!response.isSuccessful) {
            Log.e(
                "Sync",
                "Failed to load actions from server to local db:" +
                    "${response.errorBody()?.string()}"
            )
        }
        val serverActions = response.body()
        val localActions = db.actionDao().getAllActions()
        serverActions?.forEach { serverAction ->
            var new = true
            localActions.forEach { locAct -> if (serverAction.id == locAct.serverId) new = false }
            if (!new) return@forEach

            if (categoryDao.getCategoryByServerId(serverAction.categoryId) == null) {
                return@forEach
            }
            val newAction = Action(
                name = serverAction.name,
                type = serverAction.type,
                description = serverAction.description,
                value = serverAction.value,
                date = LocalDate.parse(serverAction.date, formatter),
                categoryId = categoryDao.getCategoryByServerId(serverAction.categoryId)!!.id,
                serverId = serverAction.id,
                createdAt = serverAction.createdAt
            )
            actionDao.insertAction(newAction)
        }
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
    val prefManager = PreferencesManager(application)
    val encryptedPrefManager = EncryptedPreferencesManager(application)
    val userId = prefManager.getInt("id", 0)
    val token = encryptedPrefManager.getString("token", "")

    if (token.isEmpty() || userId == 0) return
    val apiRep = ApiRepository()
    val db = FinansticsDatabase.getDatabase(application)
    val categoryDao = db.categoryDao()
    val preferencesManager = PreferencesManager(application)

    try {
        val response = apiRep.getUserCategoriesSince(userId, preferencesManager.getUpdateTime())
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

    val preferencesManager = PreferencesManager(application)
    preferencesManager.saveUpdateTime()
}
