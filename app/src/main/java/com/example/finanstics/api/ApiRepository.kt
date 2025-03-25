package com.example.finanstics.api

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
        categoryName: String
    ): Response<CategoryResponse> {
        return RetrofitInstance.api.addCategory(userId, token, categoryName)
    }

    suspend fun addAction(
        userId: Int,
        token: String,
        actionName: String,
        actionType: Int,
        value: Int,
        date: String,
        categoryId: Int,
        description: String,
        groupId: Int?
    ): Response<ActionResponse> {
        return RetrofitInstance.api.addAction(
            userId,
            token,
            actionName,
            actionType,
            value,
            date,
            categoryId,
            description,
            groupId
        )
    }
}
