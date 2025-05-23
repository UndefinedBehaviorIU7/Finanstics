package com.ub.finanstics.presentation.settings.profileSettings

import android.graphics.Bitmap
import android.net.Uri


sealed class ProfileSettingsUiState {
    data class Auth(
        val image: Bitmap?,
        val username: String,
        val userData: String?,
        val nightMode: Boolean,
        val notifications: Boolean,
    ): ProfileSettingsUiState()

    data class NotAuth(
        val nightMode: Boolean,
        val notifications: Boolean
    ): ProfileSettingsUiState()

    data class Error(
        val msg: String
    ): ProfileSettingsUiState()

    object Loading: ProfileSettingsUiState()
}