package com.ub.finanstics.presentation.groupScreens.addGroup

import android.content.Context
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager

@Suppress("TooGenericExceptionCaught")
class AddGroupRepository(context: Context) {
    private val enPrefs = EncryptedPreferencesManager(context)
    private val api = ApiRepository()

    suspend fun createGroup(state: AddGroupUiState.Idle): Boolean {
        try {

            val users: List<Int> = state.users.map { it.id }

            val response = api.createGroup(
                token = enPrefs.getString("token", ""),
                groupName = state.groupName,
                groupData = state.groupData,
                users = users
            )

            return response.isSuccessful
        } catch (_: Exception) {
            return false
        }
    }

    @Suppress("MagicNumber")
    suspend fun getUserByTag(tag: String): Int {
        try {
            val response = api.getUserByTag(tag)

            return if (response.isSuccessful) {
                response.body()!!.id
            } else {
                -1
            }
        } catch (_: Exception) {
            return -2
        }
    }
}
