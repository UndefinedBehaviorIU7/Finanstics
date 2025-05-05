package com.example.finanstics.presentation.login

import android.content.Context
import com.example.finanstics.R
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.api.models.UserResponse
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import retrofit2.Response

class LoginRepository(private val context: Context) {

    @Suppress("TooGenericExceptionCaught")
    suspend fun logIn(login: String, password: String): LoginUiState {
        return try {
            val response = RetrofitInstance.api.login(tag = login, password = password)
            handleResponse(response, login, password)
        } catch (e: Exception) {
            LoginUiState.Error(
                login = login,
                password = password,
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
    }

    @Suppress("MagicNumber")
    private fun handleResponse(
        response: Response<UserResponse>,
        login: String,
        password: String
    ): LoginUiState {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val preferencesManager = PreferencesManager(context)
                preferencesManager.saveData("id", body.id)
                preferencesManager.saveData("token", body.token)

                LoginUiState.Success(
                    id = body.id,
                    token = body.token,
                    successMsg = context.getString(R.string.log_in_success)
                )
            } else {
                LoginUiState.Error(
                    login = login,
                    password = password,
                    errorMsg = context.getString(R.string.unknown_server_error)
                )
            }
        } else {
            val errorMsgResource = when (response.code()) {
                400 -> R.string.server_error_400
                401 -> R.string.server_error_401
                404 -> R.string.server_error_404
                409 -> R.string.server_error_409
                else -> R.string.unknown_server_error
            }

            LoginUiState.Error(
                login = login,
                password = password,
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }
}
