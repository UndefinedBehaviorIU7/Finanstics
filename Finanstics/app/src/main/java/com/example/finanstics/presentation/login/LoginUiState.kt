package com.example.finanstics.presentation.login

sealed class LoginUiState {
    data class Idle (
        val login: String = "",
        val password: String = ""
    ): LoginUiState()

    data class Loading (
        val login: String,
        val password: String
    ): LoginUiState()

    data class Error (
        val login: String,
        val password: String,
        val errorMessage: String
    ): LoginUiState()

    data class Success (
        val id: Int,
        val token: String,
        val successMessage: String
    ): LoginUiState()
}
