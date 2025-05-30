package com.ub.finanstics.presentation.groupScreens.groups

import android.content.Context
import android.graphics.BitmapFactory
import coil3.Bitmap
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.GroupWithImage
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Response

class GroupsRepository(private val context: Context) {
    private val api = ApiRepository()

    @Suppress("TooGenericExceptionCaught", "MagicNumber")
    suspend fun getGroups(): GroupsUiState {
        return try {
            val preferencesManager = PreferencesManager(context)
            val userId = preferencesManager.getInt("id", -1)
            val apiRepository = ApiRepository()
            val response = apiRepository.getUserGroups(userId)
            handleGetGroups(response)

        } catch (e: Exception) {
            GroupsUiState.Error(
                groups = emptyList(),
                errorMsg = context.getString(R.string.no_internet)
            )
        }
    }

    @Suppress("MagicNumber")
    private suspend fun handleGetGroups(response: Response<List<Group>>): GroupsUiState =
        coroutineScope {
            if (response.isSuccessful) {
                val groupList = response.body()
                if (groupList != null) {
                    val groupsWithImages = groupList.map { group ->
                        val bitmapDeferred = async(Dispatchers.IO) {
                            getGroupImage(group.id)
                        }
                        val bitmap = bitmapDeferred.await()
                        GroupWithImage(group = group, image = bitmap)
                    }
                    GroupsUiState.All(groups = groupsWithImages)
                } else {
                    GroupsUiState.Error(
                        groups = emptyList(),
                        errorMsg = context.getString(R.string.unknown_server_error)
                    )
                }
            } else {
                val errorMsgResource = when (response.code()) {
                    400 -> R.string.server_error_400
                    401 -> R.string.server_error_401
                    404 -> R.string.server_error_404
                    409 -> R.string.server_error_409
                    else -> R.string.unknown_server_error
                }

                GroupsUiState.Error(
                    groups = emptyList(),
                    errorMsg = context.getString(errorMsgResource)
                )
            }
        }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    suspend fun getGroupImage(groupId: Int): Bitmap? {
        return try {
            val response = api.getGroupImage(groupId)
            if (response.isSuccessful) {
                response.body()?.byteStream().use { stream ->
                    if (stream != null) {
                        BitmapFactory.decodeStream(stream)
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
