package com.ub.finanstics.presentation.settings.profileSettings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.ub.finanstics.ui.theme.FinansticsTheme
import com.ub.finanstics.ui.theme.TIME_INIT

class ProfileSettingsViewModel(application: Application): AndroidViewModel(application) {
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
}