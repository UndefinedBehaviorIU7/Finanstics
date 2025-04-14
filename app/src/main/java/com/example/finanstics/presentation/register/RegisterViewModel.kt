package com.example.finanstics.presentation.register

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle())
    val uiState = _uiState.asStateFlow()

    fun loginChange(newLogin: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(login = newLogin)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = newLogin,
                    tag = current.tag,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image
                )
            }

            else -> Unit
        }
    }

    fun tagChange(newTag: String) {
        when (val current = _uiState.value) {
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(tag = newTag)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
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
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(password = newPassword)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
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
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(passwordRepeat = newPasswordRepeat)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
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
            is RegisterUiState.Idle -> {
                _uiState.value = current.copy(image = newImage)
            }

            is RegisterUiState.Error -> {
                _uiState.value = RegisterUiState.Idle(
                    login = current.login,
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

        if (current is RegisterUiState.Idle) {
            if (current.login.isBlank() || current.password.isBlank()) {
                _uiState.value = RegisterUiState.Error(
                    login = current.login,
                    tag = current.tag,
                    password = current.password,
                    passwordRepeat = current.passwordRepeat,
                    image = current.image,
                    errorMsg = "fields shouldn't be blank"
                )
                return
            }

            _uiState.value = RegisterUiState.Loading(
                login = current.login,
                tag = current.tag,
                password = current.password,
                passwordRepeat = current.passwordRepeat,
                image = current.image
            )

            viewModelScope.launch {
                try {
//                    if (current.password != current.passwordRepeat)  {
//                        _uiState.value = RegisterUiState.Error (
//                            login = current.login,
//                            tag = current.tag,
//                            password = current.password,
//                            passwordRepeat = current.passwordRepeat,
//                            image = current.image,
//                            errorMsg = getApplication<Application>().getString(R.string.passwords_do_not_match)
//                        )
//                    } else {
//                        val imageUri = current.image
//                        val imagePart = imageUri?.let { createMultipartBodyPart(it) }
//
//                        val request = NetworkService.api.signup(
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
                    TODO("Server exceptions")
                } catch (e: Exception) {
                    _uiState.value = RegisterUiState.Error (
                        login = current.login,
                        tag = current.tag,
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