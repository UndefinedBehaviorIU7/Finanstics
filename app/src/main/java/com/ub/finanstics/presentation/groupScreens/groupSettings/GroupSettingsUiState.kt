package com.ub.finanstics.presentation.groupScreens.groupSettings

import android.graphics.Bitmap
import android.net.Uri
import com.ub.finanstics.api.models.User

sealed class GroupSettingsUiState {
    data class Idle(
        val groupId: Int,
        val groupName: String,
        val groupData: String?,
        val imageUri: Uri? = null,
        val imageBitmap: Bitmap?,
        val owner: User,
        val users: List<Int>,
        val admins: List<Int>?,
        val members: List<User>
    ) : GroupSettingsUiState()

    data class Error(
        val errorMsg: String
    ) : GroupSettingsUiState()

    data object Loading : GroupSettingsUiState()
}
