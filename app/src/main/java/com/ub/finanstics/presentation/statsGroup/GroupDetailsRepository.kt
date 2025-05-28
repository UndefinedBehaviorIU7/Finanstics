package com.ub.finanstics.presentation.statsGroup

import android.content.Context
import android.util.Log
import com.ub.finanstics.api.ApiRepository

class GroupDetailsRepository(private val context: Context) {
    @Suppress("TooGenericExceptionCaught")
    suspend fun getUserName(
        userId: Int
    ): String? {
        var res: String? = null
        try {
            val apiRep = ApiRepository()
            val response = apiRep.getUser(userId)
            if (response.isSuccessful) {
                res = response.body()?.username
            }
        } catch (e: Exception) {
            Log.e("getUserName", "$e")
        }
        return res
    }
}
