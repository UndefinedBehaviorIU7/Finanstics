package com.ub.finanstics.api

import com.ub.finanstics.api.models.Action
import com.ub.finanstics.api.responses.ActionResponse
import com.ub.finanstics.api.responses.BaseResponse
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.api.responses.CategoryResponse
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.User
import com.ub.finanstics.api.responses.UserResponse
import com.ub.finanstics.api.responses.VKUserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

// Мультипарт конвертер
fun String.toPlainPart(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())

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
        groupId: List<Int>?
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

    // Передавать нужно путь к файлу с устройства
    suspend fun register(
        username: String,
        password: String,
        tag: String,
    ): Response<UserResponse> {
        return RetrofitInstance.api.register(
            username,
            password,
            tag,
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

    suspend fun loginVK(
        vkId: Int,
    ): Response<VKUserResponse> {
        return RetrofitInstance.api.loginVK(vkId)
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

    suspend fun getUserVK(vkId: Int): Response<User> {
        return RetrofitInstance.api.getUserVK(vkId)
    }

    suspend fun getUserByTag(tag: String): Response<User> {
        return RetrofitInstance.api.getUserByTag(tag)
    }

    @Suppress("LongParameterList")
    suspend fun registerVK(
        vkId: Int,
        username: String,
        password: String,
        tag: String,
    ): Response<UserResponse> {

        return RetrofitInstance.api.registerVK(
            vkId = vkId,
            username = username,
            password = password,
            tag = tag
        )
    }

    suspend fun getGroupById(groupId: Int): Response<Group> {
        return RetrofitInstance.api.getGroupById(groupId)
    }

    suspend fun getAllGroups(): Response<List<Group>> {
        return RetrofitInstance.api.getAllGroups()
    }

    suspend fun getUserGroups(userId: Int): Response<List<Group>> {
        return RetrofitInstance.api.getUserGroups(userId)
    }

    suspend fun logout(token: String): Response<BaseResponse> {
        return RetrofitInstance.api.logout(token)
    }

    suspend fun registerFCMToken(token: String, fcmToken: String): Response<ResponseBody> {
        return RetrofitInstance.api.registerFCMToken(token, fcmToken)
    }

    suspend fun getGroupCategories(groupId: Int): Response<List<Category>> {
        return RetrofitInstance.api.getGroupCategories(groupId)
    }

    suspend fun getGroupActionsByCategory(
        groupId: Int,
        categoryName: String,
        type: Int
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActionsByCategory(groupId, categoryName, type)
    }

    @Suppress("LongParameterList")
    suspend fun getGroupActionsByCategoryAndDate(
        groupId: Int,
        categoryName: String,
        type: Int,
        year: Int,
        month: Int,
        day: Int? = null
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActionsByCategoryAndDate(
            groupId, categoryName, type, year, month, day
        )
    }
}
