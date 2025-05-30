package com.ub.finanstics.presentation.userScreens.register

import android.content.Context
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.responses.UserResponse
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import retrofit2.Response

class RegisterRepository(private val context: Context) {
    private val api = ApiRepository()

    @Suppress("TooGenericExceptionCaught")
    suspend fun register(
        username: String,
        password: String,
        tag: String,
    ): RegisterUiState {
        return try {
            val response = api.register(
                username = username,
                password = password,
                tag = tag,
            )
            handleResponse(response, username, password, tag)
        } catch (_: Exception) {
            RegisterUiState.Error(
                login = tag,
                username = username,
                password = password,
                passwordRepeat = "",
                errorMsg = context.getString(R.string.no_internet)
            )
        }
    }

    @Suppress("MagicNumber")
    private fun handleResponse(
        response: Response<UserResponse>,
        username: String,
        password: String,
        tag: String,
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
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }

    @Suppress("MagicNumber", "LongParameterList")
    private fun handleResponseVK(
        response: Response<UserResponse>,
        vkId: Int,
        username: String,
        password: String,
        image: String,
        tag: String
    ): RegisterUiState {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val preferencesManager = PreferencesManager(context)
                val encryptedPrefManager = EncryptedPreferencesManager(context)

                val id = body.id
                val token = body.token

                preferencesManager.saveData("id", id)
                preferencesManager.saveData("vk_id", vkId)
                preferencesManager.saveData("username", username)
                preferencesManager.saveData("tag", tag)
                encryptedPrefManager.saveData("token", token)

                RegisterUiState.Success(
                    context.getString(R.string.registered)
                )
            } else {
                RegisterUiState.VKError(
                    login = tag,
                    password = password,
                    image = image,
                    username = username,
                    vkId = vkId,
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

            RegisterUiState.VKError(
                login = tag,
                password = password,
                image = image,
                username = username,
                vkId = vkId,
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }

    @Suppress("TooGenericExceptionCaught", "ReturnCount", "LongParameterList")
    suspend fun registerVK(
        vkId: Int,
        username: String,
        password: String,
        image: String,
        tag: String
    ): RegisterUiState {
        if (tagExists(tag)) {
            return RegisterUiState.VKError(
                login = tag,
                password = password,
                image = image,
                username = username,
                vkId = vkId,
                errorMsg = context.getString(R.string.tag_exists)
            )
        }
        var regState: RegisterUiState
        val userResp = api.getUserVK(vkId)
        try {
            if (!userResp.isSuccessful) {
                val resp = api.registerVK(
                    vkId = vkId,
                    username = username,
                    tag = tag,
                    password = password
                )
                return handleResponseVK(resp, vkId, username, password, image, tag)
            }
            regState = RegisterUiState.VKError(
                login = tag,
                password = password,
                image = image,
                username = username,
                vkId = vkId,
                errorMsg = context.getString(R.string.already_registered)
            )
        } catch (_: Exception) {
            regState = RegisterUiState.VKError(
                login = tag,
                password = password,
                image = image,
                username = username,
                vkId = vkId,
                errorMsg = context.getString(R.string.no_internet)
            )
        }
        return regState
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun tagExists(tag: String): Boolean {
        try {
            val resp = api.getUserByTag(tag)
            if (resp.isSuccessful) {
                return (resp.body() != null)
            }
        } catch (_: Exception) { }
        return false
    }
}
