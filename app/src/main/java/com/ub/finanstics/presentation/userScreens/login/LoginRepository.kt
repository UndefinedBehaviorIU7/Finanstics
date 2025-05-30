package com.ub.finanstics.presentation.userScreens.login

import android.content.Context
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.responses.UserResponse
import com.ub.finanstics.api.responses.VKUserResponse
import com.ub.finanstics.fcm.logFirebaseToken
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.vk.id.AccessToken
import retrofit2.Response

class LoginRepository(private val context: Context) {
    private val api = ApiRepository()

    @Suppress("TooGenericExceptionCaught")
    suspend fun logIn(login: String, password: String): LoginUiState {
        logFirebaseToken(context)
        return try {
            val response = api.login(tag = login, password = password)
            handleResponse(response, login, password)
        } catch (_: Exception) {
            LoginUiState.Error(
                login = login,
                password = password,
                errorMsg = context.getString(R.string.no_internet)
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
                val encryptedPrefManager = EncryptedPreferencesManager(context)
                preferencesManager.saveData("id", body.id)
                preferencesManager.saveData("tag", login)
                preferencesManager.saveData("password", password)
                encryptedPrefManager.saveData("token", body.token)

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
                404 -> R.string.invalid_login_or_password
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

    @Suppress("MagicNumber")
    private fun handleResponseVK(
        response: Response<VKUserResponse>,
        vk: AccessToken
    ): LoginUiState {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val preferencesManager = PreferencesManager(context)
                val encryptedPrefManager = EncryptedPreferencesManager(context)
                preferencesManager.saveData("id", body.id)
                preferencesManager.saveData("vk_id", vk.userID.toInt())
                preferencesManager.saveData("tag", body.tag)
                preferencesManager.saveData(
                    "username",
                    vk.userData.firstName + " " + vk.userData.lastName
                )
                encryptedPrefManager.saveData("token", body.token)

                LoginUiState.Success(
                    id = body.id,
                    token = body.token,
                    successMsg = context.getString(R.string.log_in_success)
                )
            } else {
                LoginUiState.Error(
                    login = "",
                    password = "",
                    errorMsg = context.getString(R.string.unknown_server_error)
                )
            }
        } else {
            val errorMsgResource = when (response.code()) {
                400 -> R.string.server_error_400
                401 -> R.string.server_error_401
                404 -> R.string.invalid_login_or_password
                409 -> R.string.server_error_409
                else -> R.string.unknown_server_error
            }

            LoginUiState.Error(
                login = "",
                password = "",
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun logInVK(vk: AccessToken): LoginUiState {
        logFirebaseToken(context)
        var logInState: LoginUiState
        val userResp = api.getUserVK(vk.userID.toInt())
        try {
            if (userResp.isSuccessful) {
                val user = userResp.body()
                if (user != null) {
                    val logResp = api.loginVK(vk.userID.toInt())
                    return handleResponseVK(logResp, vk)
                }
            }
            logInState = LoginUiState.Error(
                login = "",
                password = "",
                errorMsg = context.getString(R.string.no_user_vk_id)
            )
        } catch (_: Exception) {
            logInState = LoginUiState.Error(
                login = "",
                password = "",
                errorMsg = context.getString(R.string.no_internet)
            )
        }
        return logInState
    }
}
