package com.ub.finanstics.presentation.settings.profileSettings

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.traceEventEnd
import coil3.Bitmap
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Response

class ProfileSettingsRepository(private val context: Context) {
    private val prefs = PreferencesManager(context)

    fun isAuth(): Boolean {
        val enPrefs = EncryptedPreferencesManager(context)
        return enPrefs.getString("token", "").isNotEmpty()
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

    private suspend fun userInfoHandler(response: Response<User>): ProfileSettingsUiState = coroutineScope {
        if (!response.isSuccessful) {
            return@coroutineScope ProfileSettingsUiState.Error(msg = "Код ошибки: ${response.code()}")
        }

        val user = response.body()
            ?: return@coroutineScope ProfileSettingsUiState.Error(msg = "Неизвестная ошибка: пустое тело ответа")

        val bitmapDeferred = async(Dispatchers.IO) {
            getImage(user.id)
        }

        val night = prefs.getBoolean("nightMode", false)
        val notify = prefs.getBoolean("notifications", false)
        val username = user.username.orEmpty()
        val data = user.userData.orEmpty()

        val bitmap = bitmapDeferred.await()

        ProfileSettingsUiState.Auth(
            image = bitmap,
            username = username,
            userData = data,
            nightMode = night,
            notifications = notify
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
        val response = RetrofitInstance.api.logout(EncryptedPreferencesManager(context).getString("token", ""))
        if (response.isSuccessful) {
            return ProfileSettingsUiState.Loading
        } else {
            return ProfileSettingsUiState.Error("Неизвестная ошибка")
        }
    }
}