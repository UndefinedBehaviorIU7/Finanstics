package com.ub.finanstics.presentation.addGroup

data class User(
    val id: Int,
    val tag: String
)

sealed class AddGroupUiState {
    data class Idle (
        val groupName: String = "",
        val groupData: String = "",
        val userInput: String = "",
        val users: List<User> = emptyList(),
        val inputError: Boolean = false,
        val errorMsg: String = ""
    ): AddGroupUiState()

    data class Error (
        val msg: String
    ): AddGroupUiState()

    data object Loading: AddGroupUiState()
}