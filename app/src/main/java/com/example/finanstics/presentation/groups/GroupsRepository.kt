package com.example.finanstics.presentation.groups

import android.content.Context
import com.example.finanstics.R
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.api.models.Group
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import retrofit2.Response

class GroupsRepository(private val context: Context) {
    @Suppress("TooGenericExceptionCaught")
    suspend fun getGroups(): GroupsUiState {
        return try {
            val preferencesManager = PreferencesManager(context)
            val userId = preferencesManager.getInt("id", -1)
            val response = RetrofitInstance.api.getUserGroups(userId)
            handleResponse(response)
        } catch (e: Exception) {
            GroupsUiState.Error(
                groups = emptyList(),
                errorMsg = context.getString(R.string.unknown_error)
            )
        }
    }

    @Suppress("MagicNumber")
    private fun handleResponse(
        response: Response<List<Group>>,
    ): GroupsUiState {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                GroupsUiState.All(
                    groups = body
                )
            } else {
                GroupsUiState.Error(
                    groups = emptyList(),
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

            GroupsUiState.Error(
                groups = emptyList(),
                errorMsg = context.getString(errorMsgResource)
            )
        }
    }
}
