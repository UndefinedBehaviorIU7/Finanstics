package com.ub.finanstics.presentation.settings.profileSettings

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import coil3.Bitmap
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileSettingsRepository(private val context: Context) {
    suspend fun isAuth(): Boolean {
        val enPrefs = EncryptedPreferencesManager(context)
        return enPrefs.getString("token", "") != ""
    }

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

    private suspend fun userInfoHandler(response: Response<User>): ProfileSettingsUiState {
        if (!response.isSuccessful) {
            return ProfileSettingsUiState.Error(msg = "Код ошибки: ${response.code()}")
        }

        val user = response.body()
            ?: return ProfileSettingsUiState.Error(msg = "Неизвестная ошибка: пустое тело ответа")

        val bitmap = getImage(user.id)

        val prefs = PreferencesManager(context)
        val night = prefs.getBoolean("nightMode", false)
        val notify = prefs.getBoolean("notifications", false)

        val username = user.username!!
        val data = user.userData.orEmpty()

        return ProfileSettingsUiState.Auth(
            image = bitmap,
            username = username,
            userData = data,
            nightMode = night,
            notifications = notify
        )
    }

}