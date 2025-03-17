package com.example.finanstics.presentation.register

import android.net.Uri

sealed class SignupUiState {
    data class Idle (
        val username: String = "",
        val tag: String = "",
        val password: String = "",
        val passwordRepeat: String = "",
        val image: Uri? = null
    ): SignupUiState()

    data class Loading (
        val username: String,
        val tag: String,
        val password: String,
        val passwordRepeat: String,
        val image: Uri?
    ): SignupUiState()

    data class Error (
        val username: String,
        val tag: String,
        val password: String,
        val passwordRepeat: String,
        val image: Uri?,
        val errorMessage: String
    ): SignupUiState()

    data class Success (
        val successMessage: String
    ): SignupUiState()
}
