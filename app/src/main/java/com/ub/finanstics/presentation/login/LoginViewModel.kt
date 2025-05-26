package com.ub.finanstics.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.R
import com.ub.finanstics.fcm.regFirebaseToken
import com.vk.id.AccessToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoginRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle())
    val uiState = _uiState.asStateFlow()

    private val _vk = MutableStateFlow<AccessToken?>(null)

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

    fun resetToIdle() {
        val current = _uiState.value
        if (current is LoginUiState.Error){
            _uiState.value = LoginUiState.Idle(
                login = current.login,
                password = current.password
            )
        }
    }

    fun logIn() {
        val current = _uiState.value

        if (current is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle(
                login = current.login,
                password = current.password,
            )
        }

        if (current is LoginUiState.Idle) {
            val login = current.login
            val password = current.password

            if (login.isBlank() || password.isBlank()) {
                _uiState.value = LoginUiState.Error(
                    login = login,
                    password = password,
                    errorMsg = getApplication<Application>().getString(R.string.empty_fields)
                )
                return
            }

            _uiState.value = LoginUiState.Loading(login, password)

            viewModelScope.launch {
                val result = repository.logIn(login, password)
                regFirebaseToken(application.applicationContext)
                _uiState.value = result
            }
        }
    }

    fun loginVK() {
        val vk = _vk.value
        if (vk != null) {
            viewModelScope.launch {
                val result = repository.logInVK(vk)
                _uiState.value = result
            }
        }
    }

    fun handleOneTapAuth(vk: AccessToken) {
        _vk.value = vk
        loginVK()
    }
}
