package com.example.finanstics.presentation.register

import android.net.Uri

sealed class RegisterUiState {
    data class Idle(
        val login: String = "",
        val username: String = "",
        val password: String = "",
        val passwordRepeat: String = "",
        val image: Uri? = null
    ) : RegisterUiState()

    data class Loading(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val image: Uri?
    ) : RegisterUiState()

    data class Error(
        val login: String,
        val username: String,
        val password: String,
        val passwordRepeat: String,
        val image: Uri?,
        val errorMsg: String
    ) : RegisterUiState()

    data class Success(
        val successMsg: String
    ) : RegisterUiState()
}
