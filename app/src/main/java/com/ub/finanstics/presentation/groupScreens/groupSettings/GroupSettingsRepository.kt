package com.ub.finanstics.presentation.groupScreens.groupSettings

import android.content.Context
import android.graphics.BitmapFactory
import coil3.Bitmap
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import retrofit2.Response

class GroupSettingsRepository(private val context: Context) {
    private val api = ApiRepository()

    @Suppress("TooGenericExceptionCaught")
    suspend fun getUserById(userId: Int): User? {
        return try {
            val response = api.getUser(userId)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getUsersByIds(users: List<Int>?): List<User>? = coroutineScope {
        if (users == null) {
            null
        } else {
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
            } catch (_: Exception) {
                null
            }
        }
    }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth", "TooGenericExceptionCaught")
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
        } catch (_: Exception) {
            null
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getGroup(groupId: Int): GroupSettingsUiState {
        return try {
            val response = api.getGroupById(groupId)
            handleGetGroup(response)
        } catch (_: Exception) {
            GroupSettingsUiState.Error(
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
    }

    @Suppress("LongMethod", "MagicNumber")
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
                        val members = getUsersByIds(group.users)
                            ?: return@coroutineScope GroupSettingsUiState.Error(
                                errorMsg = context.getString(R.string.unknown_server_error)
                            )

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

    @Suppress("TooGenericExceptionCaught")
    suspend fun leaveGroup(groupId: Int): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")
            val response = api.leaveGroup(token, groupId)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun deleteGroup(groupId: Int): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")
            val response = api.deleteGroup(token, groupId)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("LongParameterList", "TooGenericExceptionCaught")
    suspend fun updateGroupInfo(
        groupId: Int,
        name: String,
        groupData: String?,
        users: List<Int>,
        admins: List<Int>
    ): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")

            val response = api.updateGroupInfo(
                groupId = groupId,
                token = token,
                name = name,
                groupData = groupData ?: "",
                users = users,
                admins = admins
            )

            if (!response.isSuccessful) {
                response.errorBody()?.close()
                return false
            }

            true
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun updateGroupImage(
        groupId: Int,
        image: MultipartBody.Part
    ): Boolean {
        return try {
            val token = EncryptedPreferencesManager(context).getString("token", "")

            val response = api.updateGroupImage(
                groupId = groupId,
                token = token,
                image = image
            )

            if (!response.isSuccessful) {
                response.errorBody()?.close()
                return false
            }

            true
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("MagicNumber", "TooGenericExceptionCaught")
    suspend fun getUserByTag(tag: String): Int? {
        try {
            val response = api.getUserByTag(tag)

            return if (response.isSuccessful) {
                response.body()!!.id
            } else {
                null
            }
        } catch (_: Exception) {
            return null
        }
    }
}
