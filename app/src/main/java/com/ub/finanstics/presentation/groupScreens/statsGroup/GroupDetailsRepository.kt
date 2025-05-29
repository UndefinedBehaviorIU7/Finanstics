package com.ub.finanstics.presentation.groupScreens.statsGroup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Suppress("TooGenericExceptionCaught")
class GroupDetailsRepository(private val context: Context) {
    suspend fun getUserName(userId: Int): String? {
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

    @Suppress("NestedBlockDepth")
    suspend fun userImage(userId: Int): Bitmap? {
        return try {
            val response = RetrofitInstance.api.getUserImage(userId)
            if (response.isSuccessful) {
                response.body()?.byteStream().use { stream ->
                    if (stream != null) {
                        BitmapFactory.decodeStream(stream)
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getUserImage(userId: Int): Bitmap? = coroutineScope {
        val bitmapDeferred = async(Dispatchers.IO) { userImage(userId) }
        bitmapDeferred.await()
    }
}
