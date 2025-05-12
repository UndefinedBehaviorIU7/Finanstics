package com.example.finanstics.api

import com.example.finanstics.api.models.Action
import com.example.finanstics.api.models.ActionResponse
import com.example.finanstics.api.models.Category
import com.example.finanstics.api.models.CategoryResponse
import com.example.finanstics.api.models.Group
import com.example.finanstics.api.models.User
import com.example.finanstics.api.models.UserResponse
import retrofit2.Response

@Suppress("TooManyFunctions")
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

    @Suppress("LongParameterList")
    suspend fun addAction(
        userId: Int,
        token: String,
        actionName: String,
        type: Int,
        value: Int,
        date: String,
        categoryId: Int,
        description: String?,
        groupId: Int?
    ): Response<ActionResponse> {
        return RetrofitInstance.api.addAction(
            userId = userId,
            token = token,
            actionName = actionName,
            actionType = type,
            value = value,
            date = date,
            categoryId = categoryId,
            description = description,
            groupId = groupId
        )
    }

    suspend fun getUserActions(userId: Int): Response<List<Action>> {
        return RetrofitInstance.api.getUserActions(userId)
    }

    suspend fun getUserCategories(
        userId: Int
    ): Response<List<Category>> {
        return RetrofitInstance.api.getUserCategories(userId)
    }

    suspend fun getUserActionsSince(
        userId: Int,
        time: String
    ): Response<List<Action>> {
        return RetrofitInstance.api.getUserActionsSince(
            userId,
            time
        )
    }

    suspend fun getUserCategoriesSince(
        userId: Int,
        time: String
    ): Response<List<Category>> {
        return RetrofitInstance.api.getUserCategoriesSince(
            userId,
            time
        )
    }

    suspend fun register(
        username: String,
        password: String,
        tag: String,
        image: String,
    ): Response<UserResponse> {
        return RetrofitInstance.api.register(
            username,
            password,
            tag,
            image
        )
    }

    suspend fun login(
        tag: String,
        password: String,
    ): Response<UserResponse> {
        return RetrofitInstance.api.login(
            tag,
            password
        )
    }

    suspend fun getGroupActions(
        groupId: Int,
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActions(
            groupId
        )
    }

    suspend fun getGroupActionsByDate(
        groupId: Int,
        year: Int,
        month: Int,
        day: Int? = null
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActionsByDate(
            groupId,
            year,
            month,
            day
        )
    }

    suspend fun getGroupById(groupId: Int): Response<Group> {
        return RetrofitInstance.api.getGroupById(groupId)
    }

    suspend fun getAllGroups(): Response<List<Group>> {
        return RetrofitInstance.api.getAllGroups()
    }
}
