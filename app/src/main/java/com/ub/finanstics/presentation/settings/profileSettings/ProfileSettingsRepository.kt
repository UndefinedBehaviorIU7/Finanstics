package com.ub.finanstics.presentation.settings.profileSettings

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import coil3.Bitmap
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.User
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileSettingsRepository(private val context: Context) {
    fun isAuth(): Boolean {
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
                        Log.e("ABOBA_1", "Response: stream null")
                        null
                    }
                }
            } else {
                Log.e("ABOBA_1", "Response: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ABOBA_1", "Response: ${e}")
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

        val prefs = PreferencesManager(context)
        val night = prefs.getBoolean("nightMode", false)
        val notify = prefs.getBoolean("notifications", false)
        val username = user.username.orEmpty()
        val data = user.userData.orEmpty()

        val bitmap = bitmapDeferred.await()

        ProfileSettingsUiState.Auth(
            image         = bitmap,
            username      = username,
            userData      = data,
            nightMode     = night,
            notifications = notify
        )
    }

}