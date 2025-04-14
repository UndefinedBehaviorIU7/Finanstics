package com.example.finanstics.presentation.register

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.R
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

@Suppress("TooGenericExceptionCaught")
class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun onLoginChange(newLogin: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(login = newLogin)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = newLogin,
                    username = current.username,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun onUsernameChange(newUsername: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(username = newUsername)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
                    username = newUsername,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun onPasswordChange(newPassword: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(password = newPassword)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
                    username = current.username,
                    password = newPassword,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun onPasswordRepeatChange(newPasswordRepeat: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(passwordRepeat = newPasswordRepeat)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
                    username = current.username,
                    password = current.password,
                    passwordRepeat = newPasswordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun onImageChange(newImage: Uri?) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(image = newImage)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
                    username = current.username,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = newImage
                )
            }

            else -> Unit
        }
    }

    fun register() {
        val current = _uiState.value

        if (current is RegisterUiState.Idle) {
            if (current.login.isBlank() || current.username.isBlank()
                || current.password.isBlank() || current.passwordRepeat.isBlank()) {
                _uiState.value = RegisterUiState.Error(
                    login = current.login,
                    username = current.username,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image,
                    errorMsg = getApplication<Application>().getString(R.string.empty_fields)
                )
                return
            }

            _uiState.value = RegisterUiState.Loading(
                login = current.login,
                username = current.username,
                password = current.password,
                passwordRepeat = current.passwordRepeat,
                image = current.image
            )

            viewModelScope.launch {
                try {
//                    if (current.password != current.passwordRepeat)  {
//                        _uiState.value = RegisterUiState.Error (
//                            login = current.login,
//                            username = current.username,
//                            password = current.password,
//                            passwordRepeat = current.passwordRepeat,
//                            image = current.image,
//                            errorMsg = getApplication<Application>().getString(R.string.passwords_do_not_match)
//                        )
//                    } else {
//                        val imageUri = current.image
//                        val imagePart = imageUri?.let { createMultipartBodyPart(it) }
//
//                        val request = RetrofitInstance.api.signup(
//                            login = current.login.toRequestBody("text/plain".toMediaTypeOrNull()),
//                            tag = current.tag.toRequestBody("text/plain".toMediaTypeOrNull()),
//                            password = current.password.toRequestBody("text/plain".toMediaTypeOrNull()),
//                            image = imagePart
//                        )
//
//                        _uiState.value = RegisterUiState.Success(
//                            successMsg = getApplication<Application>().getString(R.string.registered)
//                        )
//                    }
                } catch (e: HttpException) {
                    val currErrorMsg = when (e.code()) {
                        400 -> R.string.error_400
                        401 -> R.string.error_401
                        404 -> R.string.error_404
                        409 -> R.string.error_409
                        else -> R.string.unknown_server_error
                    }

                    _uiState.value = RegisterUiState.Error (
                        login = current.login,
                        username = current.username,
                        password = current.password,
                        passwordRepeat = current.passwordRepeat,
                        image = current.image,
                        errorMsg = getApplication<Application>().getString(currErrorMsg)
                    )
                } catch (e: Exception) {
                    _uiState.value = RegisterUiState.Error(
                        login = current.login,
                        username = current.username,
                        password = current.password,
                        passwordRepeat = current.passwordRepeat,
                        image = current.image,
                        errorMsg = getApplication<Application>().getString(R.string.unknown_error)
                    )
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val context = getApplication<Application>().applicationContext
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun createMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        val file = uriToFile(uri) ?: return null
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("img", file.name, requestFile)
    }
}
