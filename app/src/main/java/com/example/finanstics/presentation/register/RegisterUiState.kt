package com.example.finanstics.presentation.register

sealed class RegisterUiState {
    data class Idle(
        val login: String = "",
        val username: String = "",
        val password: String = "",
        val passwordRepeat: String = "",
        val image: String = ""
    ) : RegisterUiState()

    data class Loading(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val image: String
    ) : RegisterUiState()

    data class Error(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val image: String,
        val errorMsg: String
    ) : RegisterUiState()

    data class Success(
        val successMsg: String
    ) : RegisterUiState()
}
