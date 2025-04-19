package com.example.finanstics.api

import com.example.finanstics.api.models.Action
import com.example.finanstics.api.models.ActionResponse
import com.example.finanstics.api.models.CategoryResponse
import com.example.finanstics.api.models.User
import retrofit2.Response

class ApiRepository {
    suspend fun getUser(userId: Int): Response<User> {
        return RetrofitInstance.api.getUser(userId)
    }

    suspend fun addCategory(
        userId: Int,
        token: String,
        categoryName: String,
        type: Int
    ): Response<CategoryResponse> {
        return RetrofitInstance.api.addCategory(userId, token, categoryName, type)
    }

    suspend fun addAction(
        userId: Int,
        token: String,
        action: Action
    ): Response<ActionResponse> {
        return RetrofitInstance.api.addAction(
            userId = userId,
            token = token,
            actionName = action.name,
            actionType = action.type,
            value = action.value,
            date = action.date,
            categoryId = action.categoryId,
            description = action.description!!,
            groupId = action.groupId!!
        )
    }
}
