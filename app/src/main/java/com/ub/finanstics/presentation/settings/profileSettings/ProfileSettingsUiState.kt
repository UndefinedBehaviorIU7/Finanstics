package com.ub.finanstics.presentation.settings.profileSettings

import android.net.Uri
import coil3.Bitmap

sealed class ProfileSettingsUiState {
    data class Auth(
        val image: Bitmap?,
        val username: String,
        val userData: String?,
        val nightMode: Boolean,
        val notifications: Boolean
    ): ProfileSettingsUiState()

    data class NotAuth(
        val nightMode: Boolean,
        val notifications: Boolean
    ): ProfileSettingsUiState()

    data class Error(
        val msg: String
    ): ProfileSettingsUiState()
}