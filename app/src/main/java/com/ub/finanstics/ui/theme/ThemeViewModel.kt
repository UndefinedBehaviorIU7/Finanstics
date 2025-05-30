package com.ub.finanstics.ui.theme

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import com.ub.finanstics.presentation.userScreens.profileSettings.ProfileSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProfileSettingsRepository(app)

    private val initial = repo.getNightModeOverride()
        ?: run {
            val ui = app.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            ui == Configuration.UI_MODE_NIGHT_YES
        }

    private val _isDark = MutableStateFlow(initial)
    val isDark: StateFlow<Boolean> = _isDark

    fun toggleDarkMode(on: Boolean) {
        repo.saveNightModeOverride(on)
        _isDark.value = on
    }
}
