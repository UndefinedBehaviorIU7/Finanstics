package com.example.finanstics.presentation.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.R
import com.vk.id.AccessToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RegisterRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle())
    val uiState = _uiState.asStateFlow()

    private val _vk = MutableStateFlow<AccessToken?>(null)
    val vk = _vk.asStateFlow()

    @Suppress("ComplexMethod")
    fun updateField(field: String, value: Any?) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = when (field) {
                    "login" -> current.copy(login = value as String)
                    "username" -> current.copy(username = value as String)
                    "password" -> current.copy(password = value as String)
                    "passwordRepeat" -> current.copy(passwordRepeat = value as String)
                    "image" -> current.copy(image = value as String)
                    else -> current
                }
            }

            is RegisterUiState.VKIdle -> {
                _uiState.value = when (field) {
                    "login" -> current.copy(login = value as String)
                    else -> current
                }
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = if (field == "login") value as String else current.login,
                    username = if (field == "username") value as String else current.username,
                    password = if (field == "password") value as String else current.password,
                    passwordRepeat = if (field == "passwordRepeat") value as String
                    else current.passwordRepeat,
                    image = if (field == "image") value as String else current.image
                )
            }

            is RegisterUiState.VKError -> {
                _uiState.value = when (field) {
                    "login" -> current.copy(login = value as String)
                    else -> current
                }
            }

            else -> Unit
        }
    }

    fun register() {
        val current = _uiState.value

        if (current is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle(
                login = current.login,
                username = current.username,
                password = current.password,
                passwordRepeat = "",
                image = current.image
            )
        }

        if (current is RegisterUiState.Idle) {
            val login = current.login
            val username = current.username
            val password = current.password
            val passwordRepeat = current.passwordRepeat
            val image = current.image

            if (login.isBlank() || username.isBlank() || password.isBlank()) {
                _uiState.value = RegisterUiState.Error(
                    login = login,
                    username = username,
                    password = password,
                    passwordRepeat = "",
                    image = image,
                    errorMsg = getApplication<Application>().getString(R.string.empty_fields)
                )
                return
            }
            if (password != passwordRepeat) {
                _uiState.value = RegisterUiState.Error(
                    login = login,
                    username = username,
                    password = password,
                    passwordRepeat = "",
                    image = image,
                    errorMsg = getApplication<Application>()
                        .getString(R.string.passwords_do_not_match)
                )
                return
            }

            _uiState.value = RegisterUiState.Loading(
                login = login,
                username = username,
                password = password,
                passwordRepeat = passwordRepeat,
                image = image
            )

            viewModelScope.launch {
                val result = repository.register(username, password, login, image)
                _uiState.value = result
            }
        }
    }

    fun registerVK() {
        val current = _uiState.value
        if (current is RegisterUiState.VKIdle) {
            val vkId = current.vkId
            val login = current.login
            val username = current.username
            val password = current.password
            val image = current.image

            viewModelScope.launch {
                val result = repository.registerVK(vkId, username, password, image, login)
                _uiState.value = result
            }
        }
        if (current is RegisterUiState.VKError) {
            val vkId = current.vkId
            val login = current.login
            val username = current.username
            val password = current.password
            val image = current.image

            viewModelScope.launch {
                val result = repository.registerVK(vkId, username, password, image, login)
                _uiState.value = result
            }
        }
    }

    fun vkIdle(vk: AccessToken) {
        _uiState.value = RegisterUiState.VKIdle(
            username = vk.userData.firstName + " " + vk.userData.lastName,
            login = "",
            password = "",
            vkId = vk.userID.toInt(),
            image = vk.userData.photo200!!
        )
    }

    fun handleOneTapAuth(vk: AccessToken) {
        _vk.value = vk
        vkIdle(vk)
    }
}
