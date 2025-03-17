package com.example.finanstics.presentation.register

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.net.http.HttpException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<SignupUiState>(SignupUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun loginChange(newLogin: String) {
        when (val curr = _uiState.value) {
            is SignupUiState.Idle -> {
                _uiState.value = curr.copy(tag = newLogin)
            }

            is SignupUiState.Error -> {
                _uiState.value = SignupUiState.Idle(
                    username = curr.username,
                    tag = newLogin,
                    password = curr.password,
                    passwordRepeat = curr.passwordRepeat,
                    image = curr.image
                )
            }

            else -> Unit
        }
    }

    fun tagChange(newTag: String) {
        when (val current = _uiState.value) {
            is SignupUiState.Idle -> {
                _uiState.value = current.copy(tag = newTag)
            }

            is SignupUiState.Error -> {
                _uiState.value = SignupUiState.Idle(
                    username = current.username,
                    tag = newTag,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun passwordChange(newPassword: String) {
        when (val current = _uiState.value) {
            is SignupUiState.Idle -> {
                _uiState.value = current.copy(password = newPassword)
            }

            is SignupUiState.Error -> {
                _uiState.value = SignupUiState.Idle(
                    username = current.username,
                    tag = current.tag,
                    password = newPassword,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun passwordRepeatChange(newPasswordRepeat: String) {
        when (val current = _uiState.value) {
            is SignupUiState.Idle -> {
                _uiState.value = current.copy(passwordRepeat = newPasswordRepeat)
            }

            is SignupUiState.Error -> {
                _uiState.value = SignupUiState.Idle(
                    username = current.username,
                    tag = current.tag,
                    password = current.password,
                    passwordRepeat = newPasswordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun imageChange(newImage: Uri?) {
        when (val current = _uiState.value) {
            is SignupUiState.Idle -> {
                _uiState.value = current.copy(image = newImage)
            }

            is SignupUiState.Error -> {
                _uiState.value = SignupUiState.Idle(
                    username = current.username,
                    tag = current.tag,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = newImage
                )
            }

            else -> Unit
        }
    }

    fun signup() {
        val current = _uiState.value

        if (current is SignupUiState.Idle) {
            if (current.username.isBlank() || current.password.isBlank()) {
                _uiState.value = SignupUiState.Error(
                    username = current.username,
                    tag = current.tag,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image,
                    errorMessage = "fields shouldn't be blank"
                )
                return
            }

            _uiState.value = SignupUiState.Loading(
                username = current.username,
                tag = current.tag,
                password = current.password,
                passwordRepeat = current.passwordRepeat,
                image = current.image
            )

            viewModelScope.launch {
                try {
                    TODO()
                } catch (@SuppressLint("NewApi") e: HttpException) {
                    TODO()
                }
            }
        }
    }

}