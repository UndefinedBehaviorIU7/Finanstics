package com.ub.finanstics.presentation.settings.profileSettings

import android.content.Context
import android.graphics.BitmapFactory
import coil3.Bitmap
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

@Suppress("TooGenericExceptionCaught")
class ProfileSettingsRepository(private val context: Context) {
    private val prefs = PreferencesManager(context)

    fun isAuth(): Boolean {
        val enPrefs = EncryptedPreferencesManager(context)
        return enPrefs.getString("token", "").isNotEmpty()
    }

    @Suppress("NestedBlockDepth")
    private suspend fun getImage(userId: Int): Bitmap? {
        return try {
            val response = RetrofitInstance.api.getUserImage(userId)
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

    suspend fun getUserInfo(userId: Int): ProfileSettingsUiState {
        return try {
            val resp = RetrofitInstance.api.userInfo(userId)
            userInfoHandler(resp)
        } catch (e: Exception) {
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
                token = EncryptedPreferencesManager(context).getString("token", ""),
                imageBitmap = bitmap,
                imageUri = null,
                username = username,
                userData = data,
                nightMode = night,
                notifications = notify,
                userId = prefs.getInt("id", 0)
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
        val response =
            RetrofitInstance.api.logout(
                EncryptedPreferencesManager(context).getString("token", "")
            )
        if (response.isSuccessful) {
            return ProfileSettingsUiState.Loading
        } else {
            return ProfileSettingsUiState.Error("Неизвестная ошибка")
        }
    }

    suspend fun updateData(newData: String): Boolean {
        val response = RetrofitInstance.api.updateUserData(
            token = EncryptedPreferencesManager(context).getString("token", ""),
            userId = prefs.getInt("id", 0),
            userData = newData
        )

        return response.isSuccessful
    }

    suspend fun updateImage(image: MultipartBody.Part): Boolean {
        val response = RetrofitInstance.api.updateUserImage(
            userId = prefs.getInt("id", 0).toString(),
            token = EncryptedPreferencesManager(context).getString("token", "").toRequestBody(),
            image = image
        )

        return response.isSuccessful
    }
}
