package com.example.finanstics.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope as viewModelScopeLogin

class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun loginChange(newLogin: String) {
        when (val current = _uiState.value) {
            is LoginUiState.Idle -> {
                _uiState.value = current.copy(login = newLogin)
            }

            is LoginUiState.Error -> {
                _uiState.value = LoginUiState.Idle(
                    login = newLogin,
                    password = current.password
                )
            }

            else -> Unit
        }
    }

    fun passwordChange(newPassword: String) {
        when (val current = _uiState.value) {
            is LoginUiState.Idle -> {
                _uiState.value = current.copy(password = newPassword)
            }

            is LoginUiState.Error -> {
                _uiState.value = LoginUiState.Idle(
                    login = current.login,
                    password = newPassword,
                )
            }

            else -> Unit
        }
    }

    fun auth() {
        val currState = _uiState.value

        if (currState is LoginUiState.Idle) {
            if (currState.login.isBlank()) {
                _uiState.value = LoginUiState.Error(
                    login = currState.login,
                    password = currState.password,
                    errorMessage = "Логин не введён"
                )
                return
            }
            if (currState.password.isBlank()) {
                _uiState.value = LoginUiState.Error(
                    login = currState.login,
                    password = currState.password,
                    errorMessage = "Пароль не введён"
                )
                return
            }

            _uiState.value = LoginUiState.Loading(
                login = currState.login,
                password = currState.password
            )

            viewModelScopeLogin.launch {
                TODO("Api request")
            }
        }
    }
}
