package com.ub.finanstics.presentation.register

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ub.finanstics.R
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.api.models.UserResponse
import com.ub.finanstics.fcm.FinansticsFMS
import com.ub.finanstics.api.toPlainPart
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                username = username.toPlainPart(),
                password = password.toPlainPart(),
                tag = tag.toPlainPart(),
                userData = "".toPlainPart(),
                image = MultipartBody.Part.createFormData(
                    name     = "image",
                    filename = "empty.jpg",
                    body     = ByteArray(0).toRequestBody()
                )
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
                400 -> R.string.server_error_400
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

    @Suppress("TooGenericExceptionCaught", "ReturnCount")
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
        val apiRep = ApiRepository()
        val userResp = apiRep.getUserVK(vkId)
        try {
            if (!userResp.isSuccessful) {
                val resp = apiRep.registerVK(
                    vkId = vkId,
                    username = username,
                    image = image,
                    tag = tag,
                    userData = "",
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
        } catch (e: Exception) {
            regState = RegisterUiState.VKError(
                login = tag,
                password = password,
                image = image,
                username = username,
                vkId = vkId,
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
        return regState
    }
}

@Suppress("TooGenericExceptionCaught")
suspend fun tagExists(tag: String): Boolean {
    val apiRep = ApiRepository()
    try {
        val resp = apiRep.getUserByTag(tag)
        if (resp.isSuccessful) {
            return (resp.body() != null)
        }
    } catch (_: Exception) { }
    return false
}
