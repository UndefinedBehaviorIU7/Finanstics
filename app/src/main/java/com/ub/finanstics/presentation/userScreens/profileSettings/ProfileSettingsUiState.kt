package com.ub.finanstics.presentation.userScreens.profileSettings

import android.graphics.Bitmap
import android.net.Uri

sealed class ProfileSettingsUiState {
    data class Auth(
        val userId: Int,
        val username: String,
        val tag: String,
        val token: String,
        val userData: String,
        val imageUri: Uri?,
        val imageBitmap: Bitmap?,
        val nightMode: Boolean,
        val notifications: Boolean,
        val showPasswordDialog: Boolean,
        val passwordChangeError: Boolean,
        val showPasswordChangeToast: Boolean
    ) : ProfileSettingsUiState()

    data class NotAuth(
        val nightMode: Boolean,
        val notifications: Boolean
    ) : ProfileSettingsUiState()

    data class Error(
        val msg: String
    ) : ProfileSettingsUiState()

    object Loading : ProfileSettingsUiState()
}
