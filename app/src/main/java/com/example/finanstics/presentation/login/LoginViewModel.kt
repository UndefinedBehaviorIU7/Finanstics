package com.example.finanstics.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.presentation.login.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoginRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun updateField(field: String, newValue: String) {
        val current = _uiState.value
        _uiState.value = when (current) {
            is LoginUiState.Idle -> when (field) {
                "login" -> current.copy(login = newValue)
                "password" -> current.copy(password = newValue)
                else -> current
            }

            is LoginUiState.Error -> LoginUiState.Idle(
                login = if (field == "login") newValue else current.login,
                password = if (field == "password") newValue else current.password
            )

            else -> current
        }
    }

    fun logIn() {
        val current = _uiState.value
        if (current !is LoginUiState.Idle) return

        _uiState.value = LoginUiState.Loading(current.login, current.password)

        viewModelScope.launch {
            val result = repository.login(current.login, current.password)
            _uiState.value = result
        }
    }
}
