package com.example.finanstics.api

import com.example.finanstics.api.models.CreateActionResponse
import com.example.finanstics.api.models.CreateCategoryResponse
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
    ): Response<CreateCategoryResponse> {
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
    ): Response<CreateActionResponse> {
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
