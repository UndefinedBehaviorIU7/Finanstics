package com.ub.finanstics.presentation.addGroup

import androidx.compose.ui.geometry.Size

sealed class AddGroupUiState {
    data class Idle (
        val groupName: String = "",
        val groupData: String = "",
        val admins: MutableList<Int> = mutableListOf(),
        val users: MutableList<Int> = mutableListOf(),
    ): AddGroupUiState()

    data class Error (
        val msg: String
    ): AddGroupUiState()

    data object Loading: AddGroupUiState()
}