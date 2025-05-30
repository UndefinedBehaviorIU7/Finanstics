package com.ub.finanstics.presentation.userScreens.profileSettings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.R
import com.ub.finanstics.TIME_INIT
import com.ub.finanstics.converters.createMultipartBodyPart
import com.ub.finanstics.converters.uriToBitmap
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Suppress("DEPRECATION", "TooManyFunctions")
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

    fun saveUsername(newUsername: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                viewModelScope.launch {
                    if (repository.updateUsername(newUsername)) {
                        _uiState.value = current.copy()
                    } else {
                        _uiState.value = ProfileSettingsUiState.Error(
                            getApplication<Application>().getString(R.string.unknown_error)
                        )
                    }
                }
            }

            else -> Unit
        }
    }

    fun onDataChange(newData: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                _uiState.value = current.copy(userData = newData)
            }

            else -> Unit
        }
    }

    fun onUsernameChange(newUsername: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                _uiState.value = current.copy(username = newUsername)
            }

            else -> Unit
        }
    }

    fun onShowPasswordChange(value: Boolean) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                _uiState.value = current.copy(showPasswordDialog = value)
            }

            else -> Unit
        }
    }

    fun onShowPasswordToastChange(value: Boolean) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                _uiState.value = current.copy(showPasswordChangeToast = value)
            }

            else -> Unit
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        when (val current = _uiState.value) {
            is ProfileSettingsUiState.Auth -> {
                viewModelScope.launch {
                    val response = repository.changePassword(oldPassword, newPassword)
                    if (response) {
                        _uiState.value = current.copy(showPasswordChangeToast = true,
                            showPasswordDialog = false)
                    } else {
                        _uiState.value = current.copy(passwordChangeError = true)
                    }
                }
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
                        _uiState.value = ProfileSettingsUiState.Error(
                            getApplication<Application>().getString(R.string.unknown_error)
                        )
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

                val imagePart = createMultipartBodyPart(getApplication<Application>(), uri) ?: run {
                    _uiState.value = ProfileSettingsUiState.Error(
                        getApplication<Application>().getString(R.string.file_processing_error)
                    )
                    return
                }

                val newBitmap = uriToBitmap(getApplication<Application>(), uri)
                viewModelScope.launch {
                    val success = repository.updateImage(imagePart)
                    if (success) {
                        _uiState.value = current.copy(
                            imageUri = uri,
                            imageBitmap = newBitmap
                        )
                    } else {
                        _uiState.value = ProfileSettingsUiState.Error(
                            getApplication<Application>().getString(R.string.server_error)
                        )
                    }
                }
            }
            else -> Unit
        }
    }

    fun offlineMode(isDark: Boolean) {
        _uiState.value = ProfileSettingsUiState.NotAuth(
            nightMode = isDark,
            notifications = false
        )
    }
}
