package com.example.finanstics.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Suppress("TooGenericExceptionCaught")
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun onLoginChange(newLogin: String) {
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

    fun onPasswordChange(newPassword: String) {
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

    fun login() {
        val current = _uiState.value

        if (current is LoginUiState.Idle) {
            if (current.login.isBlank() || current.password.isBlank()) {
                _uiState.value = LoginUiState.Error(
                    login = current.login,
                    password = current.password,
                    errorMsg = getApplication<Application>().getString(R.string.empty_fields)
                )
                return
            }

            _uiState.value = LoginUiState.Loading(
                login = current.login,
                password = current.password
            )

            viewModelScope.launch {
                try {
//                    TODO("Auth")
//                    val request = RetrofitInstance.api.auth(
//                        login = current.login,
//                        password = current.password
//                    )
//                    val id = request.id
//                    val token = request.token
//
//                    val sharedPref = getApplication<Application>()
//                        .getSharedPreferences("auth", Context.MODE_PRIVATE)
//
//                    sharedPref.edit {
//                        putString("token", token)
//                            .putInt("id", id)
//                    }
//
//                    _uiState.value = LoginUiState.Success(
//                        id = id,
//                        token = token,
//                        successMsg = getApplication<Application>().getString(R.string.login_success)
//                    )
                } catch (e: HttpException) {
                    val currErrorMsg = when (e.code()) {
                        400 -> R.string.error_400
                        401 -> R.string.error_401
                        404 -> R.string.error_404
                        409 -> R.string.error_409
                        else -> R.string.unknown_server_error
                    }

                    _uiState.value = LoginUiState.Error (
                        login = current.login,
                        password = current.password,
                        errorMsg = getApplication<Application>().getString(currErrorMsg)
                    )
                } catch (e: Exception) {
                    _uiState.value = LoginUiState.Error(
                        login = current.login,
                        password = current.password,
                        errorMsg = getApplication<Application>().getString(R.string.unknown_error)
                    )
                }
            }
        }
    }
}
