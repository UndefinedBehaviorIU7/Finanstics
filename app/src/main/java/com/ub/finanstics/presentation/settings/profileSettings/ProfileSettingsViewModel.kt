package com.ub.finanstics.presentation.settings.profileSettings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileSettingsViewModel(application: Application): AndroidViewModel(application) {
    private val repository = ProfileSettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<ProfileSettingsUiState>(
        ProfileSettingsUiState.NotAuth(
            nightMode = false,
            notifications = false
        )
    )
    val uiState: StateFlow<ProfileSettingsUiState> = _uiState

    init {
        if (repository.isAuth()) {
            load()
        } else {
            _uiState.value = ProfileSettingsUiState.NotAuth(false, false)
        }
    }

    fun load() {
        _uiState.value = ProfileSettingsUiState.Loading(isLoading = true)
        viewModelScope.launch {
            val prefs = PreferencesManager(getApplication())
            if (repository.isAuth()) {
                _uiState.value = repository.getUserInfo(prefs.getInt("id", -1))
            }
        }
    }

    fun logout() {
        _uiState.value = ProfileSettingsUiState.Loading(isLoading = true)

    }
}