package com.ub.finanstics.presentation.userScreens.register

sealed class RegisterUiState {
    data class Idle(
        val login: String = "",
        val username: String = "",
        val password: String = "",
        val passwordRepeat: String = "",
    ) : RegisterUiState()

    data class Loading(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val image: String,
        val vkId: Int? = null,
    ) : RegisterUiState()

    data class Error(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val errorMsg: String
    ) : RegisterUiState()

    data class Success(
        val successMsg: String
    ) : RegisterUiState()

    data class VKIdle(
        val login: String,
        val username: String,
        val password: String,
        val image: String,
        val vkId: Int,
    ) : RegisterUiState()

    data class VKError(
        val login: String,
        val username: String,
        val password: String,
        val image: String,
        val vkId: Int,
        val errorMsg: String
    ) : RegisterUiState()
}
