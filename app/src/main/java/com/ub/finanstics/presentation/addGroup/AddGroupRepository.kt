package com.ub.finanstics.presentation.addGroup

import android.content.Context
import com.google.gson.Gson

import com.ub.finanstics.api.RetrofitInstance
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AddGroupRepository(private val context: Context) {
    private val prefs = PreferencesManager(context)
    private val enPrefs = EncryptedPreferencesManager(context)

    suspend fun createGroup(state: AddGroupUiState.Idle): Boolean {
        try {
            val gson = Gson()
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()

            val users: List<Int> = state.users.map { it.id }

            val response = RetrofitInstance.api.createGroup(
                token = enPrefs.getString("token", "").toRequestBody(),
                groupName = state.groupName.toRequestBody("text/plain".toMediaType()),
                groupData = state.groupData.toRequestBody("text/plain".toMediaType()),
                users = gson.toJson(users).toRequestBody(jsonMediaType),
            )

            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getUserByTag(tag: String): Int {
        try {
            val response = RetrofitInstance.api.getUserByTag(tag)

            return if (response.isSuccessful) {
                response.body()!!.id
            } else {
                -1
            }
        } catch (e: Exception) {
            return -2
        }
    }
}
