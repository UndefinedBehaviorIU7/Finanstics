package com.example.finanstics.presentation.register

import android.content.Context
import com.example.finanstics.R
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.api.models.UserResponse
import com.example.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import retrofit2.Response

class RegisterRepository(private val context: Context) {

    @Suppress("TooGenericExceptionCaught")
    suspend fun register(
        username: String,
        password: String,
        tag: String,
        image: String,
    ): RegisterUiState {
        return try {
            val response = RetrofitInstance.api.register(
                username = username,
                password = password,
                tag = tag,
                image = "" // TODO("API")
            )
            handleResponse(response, username, password, tag, image)
        } catch (e: Exception) {
            RegisterUiState.Error(
                login = tag,
                username = username,
                password = password,
                passwordRepeat = "",
                image = image,
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
    }

    @Suppress("MagicNumber")
    private fun handleResponse(
        response: Response<UserResponse>,
        username: String,
        password: String,
        tag: String,
        image: String
    ): RegisterUiState {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val preferencesManager = PreferencesManager(context)
                val encryptedPrefManager = EncryptedPreferencesManager(context)
                preferencesManager.saveData("id", body.id)
                preferencesManager.saveData("tag", tag)
                encryptedPrefManager.saveData("token", body.token)
                RegisterUiState.Success(
                    successMsg = context.getString(R.string.registered)
                )
            } else {
                RegisterUiState.Error(
                    login = tag,
                    username = username,
                    password = password,
                    passwordRepeat = "",
                    image = image,
                    errorMsg = context.getString(R.string.unknown_server_error)
                )
            }
        } else {
            val errorMsgResource = when (response.code()) {
                400 -> R.string.already_registered
                401 -> R.string.server_error_401
                404 -> R.string.server_error_404
                409 -> R.string.server_error_409
                else -> R.string.unknown_server_error
            }

            RegisterUiState.Error(
                login = tag,
                username = username,
                password = password,
                passwordRepeat = "",
                image = image,
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }
}
