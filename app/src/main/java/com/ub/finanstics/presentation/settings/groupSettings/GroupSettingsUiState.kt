package com.ub.finanstics.presentation.settings.groupSettings

import android.graphics.Bitmap
import android.net.Uri
import com.ub.finanstics.api.models.User

sealed class GroupSettingsUiState {
    data class Idle(
        val groupId: Int = -1,
        val groupName: String = "",
        val groupData: String? = "",
        val imageUri: Uri? = null,
        val imageBitmap: Bitmap? = null,
        val owner: User,
        val users: List<Int>? = null,
        val admins: List<Int>? = null,
        val members: List<User>? = null
    ) : GroupSettingsUiState()

    data class Error(
        val errorMsg: String
    ) : GroupSettingsUiState()

    data object Loading : GroupSettingsUiState()
}
