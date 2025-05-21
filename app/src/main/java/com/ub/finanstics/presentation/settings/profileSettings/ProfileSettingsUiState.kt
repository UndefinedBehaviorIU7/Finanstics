package com.ub.finanstics.presentation.settings.profileSettings

import android.net.Uri

sealed class ProfileSettingsUiState {
    data class Auth(
        val image: Uri?,
        val username: String,
        val userData: String,
        val nightMode: Boolean,
        val notifications: Boolean
    ): ProfileSettingsUiState()

    data class NotAuth(
        val nightMode: Boolean,
        val notifications: Boolean
    )
}