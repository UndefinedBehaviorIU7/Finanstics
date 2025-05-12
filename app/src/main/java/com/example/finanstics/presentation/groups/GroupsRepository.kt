package com.example.finanstics.presentation.groups

import android.content.Context
import com.example.finanstics.R
import com.example.finanstics.api.RetrofitInstance
import com.example.finanstics.api.models.Group
import retrofit2.Response


class GroupsRepository(private val context: Context) {
    @Suppress("TooGenericExceptionCaught")
    suspend fun getGroups(): GroupsUiState {
        return try {
            val response = RetrofitInstance.api.getAllGroups()
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
                    groups = emptyList()
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
