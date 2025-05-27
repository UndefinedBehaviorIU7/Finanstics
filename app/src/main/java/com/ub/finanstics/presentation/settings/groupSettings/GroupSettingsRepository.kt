package com.ub.finanstics.presentation.settings.groupSettings

import android.content.Context
import android.graphics.BitmapFactory
import coil3.Bitmap
import com.google.gson.Gson
import com.ub.finanstics.R
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class GroupSettingsRepository(private val context: Context) {
    suspend fun getUserById(userId: Int): User? {
        return try {
            val response = RetrofitInstance.api.getUser(userId)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByIds(users: List<Int>): List<User>? = coroutineScope {
        try {
            val deferredResults = users.map { userId ->
                async { getUserById(userId) }
            }

            val result = mutableListOf<User>()
            for (deferred in deferredResults) {
                deferred.await()?.let { user ->
                    result.add(user)
                } ?: return@coroutineScope null
            }
            result
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    suspend fun getGroupImage(groupId: Int): Bitmap? {
        return try {
            val response = RetrofitInstance.api.getGroupImage(groupId)
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

    suspend fun getGroup(groupId: Int): GroupSettingsUiState {
        return try {
            val response = RetrofitInstance.api.getGroupById(groupId)
            handleGetGroup(response)

        } catch (e: Exception) {
            GroupSettingsUiState.Error(
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
    }

    private suspend fun handleGetGroup(response: Response<Group>): GroupSettingsUiState =
        coroutineScope {
            if (response.isSuccessful) {
                val group = response.body()
                if (group != null) {
                    val owner = getUserById(group.ownerId)

                    if (owner == null) {
                        GroupSettingsUiState.Error(
                            errorMsg = context.getString(R.string.unknown_server_error)
                        )
                    } else {
                        if (group.users == null) {
                            val bitmapDeferred = async(Dispatchers.IO) {
                                getGroupImage(group.id)
                            }
                            val bitmap = bitmapDeferred.await()

                            GroupSettingsUiState.Idle(
                                groupId = group.id,
                                groupName = group.name,
                                groupData = group.groupData,
                                imageUri = null,
                                imageBitmap = bitmap,
                                owner = owner,
                                users = group.users,
                                admins = group.admins,
                                members = null
                            )
                        } else {
                            val members = getUsersByIds(group.users)

                            if (members == null) {
                                GroupSettingsUiState.Error(
                                    errorMsg = context.getString(R.string.unknown_server_error)
                                )
                            }

                            val bitmapDeferred = async(Dispatchers.IO) {
                                getGroupImage(group.id)
                            }
                            val bitmap = bitmapDeferred.await()

                            GroupSettingsUiState.Idle(
                                groupId = group.id,
                                groupName = group.name,
                                groupData = group.groupData,
                                imageUri = null,
                                imageBitmap = bitmap,
                                owner = owner,
                                users = group.users,
                                admins = group.admins,
                                members = members
                            )
                        }
                    }
                } else {
                    GroupSettingsUiState.Error(
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

                GroupSettingsUiState.Error(
                    errorMsg = context.getString(errorMsgResource)
                )
            }
        }

    suspend fun leaveGroup(groupId: Int): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")
            val response = RetrofitInstance.api.leaveGroup(groupId, token)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteGroup(groupId: Int): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")
            val response = RetrofitInstance.api.deleteGroup(groupId, token)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateGroup(
        groupId: Int,
        name: String,
        data: String?,
        users: List<Int>?,
        admins: List<Int>?,
        image: MultipartBody.Part?
    ): Boolean {
        return try {
            val gson = Gson()
            val mediaTypeText = "text/plain".toMediaType()
            val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

            val token = EncryptedPreferencesManager(context)
                .getString("token", "")
                .toRequestBody(mediaTypeText)

            val groupIdBody = groupId.toString().toRequestBody(mediaTypeText)
            val nameBody = name.toRequestBody(mediaTypeText)
            val dataBody = (data ?: "").toRequestBody(mediaTypeText)

            val usersBody = gson.toJson(users ?: emptyList<Int>()).toRequestBody(mediaTypeJson)
            val adminsBody = gson.toJson(admins ?: emptyList<Int>()).toRequestBody(mediaTypeJson)

            val response = RetrofitInstance.api.updateGroupData(
                groupId = groupIdBody,
                token = token,
                name = nameBody,
                groupData = dataBody,
                users = usersBody,
                admins = adminsBody,
                image = image
            )

            if (!response.isSuccessful) {
                response.errorBody()?.close()
                return false
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}