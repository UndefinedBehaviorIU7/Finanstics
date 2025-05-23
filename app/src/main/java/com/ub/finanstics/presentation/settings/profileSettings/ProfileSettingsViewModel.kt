package com.ub.finanstics.presentation.settings.profileSettings

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import com.ub.finanstics.ui.theme.TIME_INIT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class ProfileSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileSettingsRepository(application.applicationContext)
    private val _uiState = MutableStateFlow<ProfileSettingsUiState>(ProfileSettingsUiState.Loading)

    val uiState: StateFlow<ProfileSettingsUiState> = _uiState

    fun onScreenEnter() {
        if (repository.isAuth()) {
            load()
        } else {
            _uiState.value = ProfileSettingsUiState.NotAuth(false, false)
        }
    }

    fun load() {
        _uiState.value = ProfileSettingsUiState.Loading
        viewModelScope.launch {
            val prefs = PreferencesManager(getApplication())
            if (repository.isAuth()) {
                _uiState.value = repository.getUserInfo(prefs.getInt("id", -1))
            }
        }
    }

    fun logout() {
        _uiState.value = ProfileSettingsUiState.Loading
        viewModelScope.launch {
            _uiState.value = repository.logout()
        }
        val prefManager = PreferencesManager(application)
        val encryptedPrefManager = EncryptedPreferencesManager(application)
        prefManager.saveData("id", 0)
        prefManager.saveData("tag", "")
        prefManager.saveData("time_update", TIME_INIT)
        encryptedPrefManager.saveData("token", "")
    }

    fun onDataChange(newData: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                _uiState.value = current.copy(userData = newData)
            }

            else -> Unit
        }
    }

    fun saveUserData(newData: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                viewModelScope.launch {
                    if (repository.updateData(newData)) {
                        _uiState.value = current.copy()
                    } else {
                        _uiState.value = ProfileSettingsUiState.Error("Неизвестная ошибка")
                    }
                }
            }

            else -> Unit
        }
    }

    fun imageChange(uri: Uri?) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                if (uri == null) return

                val imagePart = createMultipartBodyPart(uri) ?: run {
                    _uiState.value = ProfileSettingsUiState.Error("Не удалось подготовить файл")
                    return
                }

                val newBitmap = uriToBitmap(getApplication(), uri)

                viewModelScope.launch {
                    val success = repository.updateImage(imagePart)
                    if (success) {
                        _uiState.value = current.copy(
                            imageUri    = uri,
                            imageBitmap = newBitmap
                        )
                    } else {
                        _uiState.value = ProfileSettingsUiState.Error("Сервер вернул ошибку")
                    }
                }
            }
            else -> Unit
        }
    }

    private fun createMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        val context = getApplication<Application>().applicationContext
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        val requestFile = file
            .asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = requestFile
        )
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}
