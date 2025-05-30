package com.ub.finanstics.presentation.userScreens.profileSettings

import android.content.Context
import android.graphics.BitmapFactory
import coil3.Bitmap
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import retrofit2.Response

@Suppress("TooGenericExceptionCaught", "TooManyFunctions")
class ProfileSettingsRepository(private val context: Context) {
    private val prefs = PreferencesManager(context)
    private val encryptedPrefs = EncryptedPreferencesManager(context)
    private val api = ApiRepository()

    fun isAuth(): Boolean {
        return encryptedPrefs.getString("token", "").isNotEmpty()
    }

    @Suppress("NestedBlockDepth")
    private suspend fun getImage(userId: Int): Bitmap? {
        return try {
            val response = api.getUserImage(userId)
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

    suspend fun getUserInfo(userId: Int): ProfileSettingsUiState {
        return try {
            val resp = api.getUser(userId)
            userInfoHandler(resp)
        } catch (_: Exception) {
            return ProfileSettingsUiState.Error(msg = "Ошибка загрузки профиля")
        }
    }

    private suspend fun userInfoHandler(response: Response<User>): ProfileSettingsUiState =
        coroutineScope {
            if (!response.isSuccessful) {
                return@coroutineScope ProfileSettingsUiState.Error(
                    msg = "Код ошибки: ${response.code()}"
                )
            }

            val user = response.body()
                ?: return@coroutineScope ProfileSettingsUiState.Error(
                    msg = "Неизвестная ошибка: пустое тело ответа"
                )

            val bitmapDeferred = async(Dispatchers.IO) {
                getImage(user.id)
            }

            val night = prefs.getBoolean("nightMode", false)
            val notify = prefs.getBoolean("notifications", false)
            val username = user.username.orEmpty()
            val data = user.userData.orEmpty()

            val bitmap = bitmapDeferred.await()

            ProfileSettingsUiState.Auth(
                token = encryptedPrefs.getString("token", ""),
                imageBitmap = bitmap,
                imageUri = null,
                username = username,
                userData = data,
                nightMode = night,
                notifications = notify,
                userId = prefs.getInt("id", 0),
                tag = prefs.getString("tag", ""),
                showPasswordDialog = false,
                passwordChangeError = false,
                showPasswordChangeToast = false
            )
        }

    private fun hasNightModeOverride(): Boolean =
        prefs.contains("nightMode")

    fun getNightModeOverride(): Boolean? =
        if (hasNightModeOverride())
            prefs.getBoolean("nightMode", false)
        else
            null

    fun saveNightModeOverride(on: Boolean) {
        prefs.saveData("nightMode", on)
    }

    suspend fun logout(): ProfileSettingsUiState {
        val response = api.logout(
            encryptedPrefs.getString("token", "")
        )
        return if (response.isSuccessful) {
            ProfileSettingsUiState.Loading
        } else {
            ProfileSettingsUiState.Error("Неизвестная ошибка")
        }
    }

    suspend fun updateData(newData: String): Boolean {
        try {
            val response = api.updateUserData(
                token = encryptedPrefs.getString("token", ""),
                userId = prefs.getInt("id", 0),
                userData = newData
            )

            return response.isSuccessful
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun updateUsername(newUsername: String): Boolean {
        try {
            val response = api.updateUsername(
                userId = prefs.getInt("id", 0),
                token = encryptedPrefs.getString("token", ""),
                username = newUsername
            )

            return response.isSuccessful
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun updateImage(image: MultipartBody.Part): Boolean {
        try {
            val response = api.updateUserImage(
                userId = prefs.getInt("id", 0),
                token = encryptedPrefs.getString("token", ""),
                image = image
            )

            return response.isSuccessful
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        try {
            val response = api.passwordChange(
                newPassword = newPassword,
                oldPassword = oldPassword,
                userId = prefs.getInt("id", -1),
                token = encryptedPrefs.getString("token", "")
            )

            return response.isSuccessful
        } catch (_: Exception) {
            return false
        }
    }
}
