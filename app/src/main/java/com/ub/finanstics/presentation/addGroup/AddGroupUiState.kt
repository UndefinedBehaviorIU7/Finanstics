package com.ub.finanstics.presentation.addGroup

import androidx.compose.ui.geometry.Size

data class User(
    val id: Int,
    val tag: String
)

sealed class AddGroupUiState {
    data class Idle (
        val groupName: String = "",
        val groupData: String = "",
        val userInput: String = "",
        val users: MutableList<User> = mutableListOf(),
        val tagInputErr: Boolean = false,
        val errorMsg: String = ""
    ): AddGroupUiState()

    data class Error (
        val msg: String
    ): AddGroupUiState()

    data object Loading: AddGroupUiState()
}