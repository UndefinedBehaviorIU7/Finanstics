package com.ub.finanstics.api

import com.google.gson.Gson
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.User
import com.ub.finanstics.api.responses.ActionResponse
import com.ub.finanstics.api.responses.BaseResponse
import com.ub.finanstics.api.responses.CategoryResponse
import com.ub.finanstics.api.responses.UserResponse
import com.ub.finanstics.api.responses.VKUserResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

@Suppress("TooManyFunctions")
class ApiRepository {
    suspend fun registerFCMToken(token: String, fcmToken: String): Response<ResponseBody> {
        return RetrofitInstance.api.registerFCMToken(token, fcmToken)
    }

    suspend fun passwordChange(
        userId: Int,
        token: String,
        oldPassword: String,
        newPassword: String,
    ): Response<BaseResponse> {
        return RetrofitInstance.api.passwordChange(
            userId = userId,
            token = token,
            oldPassword = oldPassword,
            newPassword = newPassword
        )
    }

    suspend fun register(
        username: String,
        password: String,
        tag: String,
    ): Response<UserResponse> {
        return RetrofitInstance.api.register(
            username = username,
            password = password,
            tag = tag
        )
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

    suspend fun login(tag: String, password: String): Response<UserResponse> {
        return RetrofitInstance.api.login(tag, password)
    }

    suspend fun loginVK(vkId: Int): Response<VKUserResponse> {
        return RetrofitInstance.api.loginVK(vkId)
    }

    suspend fun logout(token: String): Response<BaseResponse> {
        return RetrofitInstance.api.logout(token)
    }

    suspend fun getUser(userId: Int): Response<User> {
        return RetrofitInstance.api.getUser(userId)
    }

    suspend fun getUserByTag(tag: String): Response<User> {
        return RetrofitInstance.api.getUserByTag(tag)
    }

    suspend fun getUserVK(vkId: Int): Response<User> {
        return RetrofitInstance.api.getUserVK(vkId)
    }

    suspend fun getUserImage(userId: Int): Response<ResponseBody> {
        return RetrofitInstance.api.getUserImage(userId)
    }

    suspend fun updateUserData(
        token: String,
        userId: Int,
        userData: String
    ): Response<BaseResponse> {
        return RetrofitInstance.api.updateUserData(
            token = token,
            userId = userId,
            userData = userData
        )
    }

    suspend fun updateUsername(
        token: String,
        userId: Int,
        username: String
    ): Response<BaseResponse> {
        return RetrofitInstance.api.updateUsername(
            token = token,
            userId = userId,
            username = username
        )
    }

    suspend fun updateUserImage(
        token: String,
        userId: Int,
        image: MultipartBody.Part
    ): Response<BaseResponse> {
        return RetrofitInstance.api.updateUserImage(
            token = token.toRequestBody(),
            userId = userId.toString(),
            image = image
        )
    }

    suspend fun createGroup(
        token: String,
        groupName: String,
        groupData: String,
        users: List<Int>
    ): Response<BaseResponse> {
        val gson = Gson()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        return RetrofitInstance.api.createGroup(
            token = token.toRequestBody(),
            groupName = groupName.toRequestBody("text/plain".toMediaType()),
            groupData = groupData.toRequestBody("text/plain".toMediaType()),
            users = gson.toJson(users).toRequestBody(jsonMediaType),
        )
    }

    suspend fun deleteGroup(token: String, groupId: Int): Response<BaseResponse> {
        return RetrofitInstance.api.deleteGroup(groupId, token)
    }

    suspend fun leaveGroup(token: String, groupId: Int): Response<BaseResponse> {
        return RetrofitInstance.api.leaveGroup(groupId, token)
    }

    suspend fun getGroupById(groupId: Int): Response<Group> {
        return RetrofitInstance.api.getGroupById(groupId)
    }

    suspend fun getUserGroups(userId: Int): Response<List<Group>> {
        return RetrofitInstance.api.getUserGroups(userId)
    }

    suspend fun getGroupImage(groupId: Int): Response<ResponseBody> {
        return RetrofitInstance.api.getGroupImage(groupId)
    }

    @Suppress("LongParameterList")
    suspend fun updateGroupInfo(
        token: String,
        groupId: Int,
        name: String,
        groupData: String?,
        users: List<Int>,
        admins: List<Int>
    ): Response<BaseResponse> {
        val gson = Gson()
        val usersJson = gson.toJson(users)
        val adminsJson = gson.toJson(admins)

        return RetrofitInstance.api.updateGroupInfo(
            token = token,
            groupId = groupId,
            name = name,
            groupData = groupData ?: "",
            users = usersJson,
            admins = adminsJson
        )
    }

    suspend fun updateGroupImage(
        token: String,
        groupId: Int,
        image: MultipartBody.Part
    ): Response<BaseResponse> {
        val mediaTypeText = "text/plain".toMediaType()

        return RetrofitInstance.api.updateGroupImage(
            token = token.toRequestBody(mediaTypeText),
            groupId = groupId.toString().toRequestBody(mediaTypeText),
            image = image
        )
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

    suspend fun getUserActionsSince(userId: Int, time: String): Response<List<Action>> {
        return RetrofitInstance.api.getUserActionsSince(userId, time)
    }

    suspend fun getGroupActions(groupId: Int): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActions(groupId)
    }

    suspend fun getGroupActionsByDate(
        groupId: Int,
        year: Int,
        month: Int,
        day: Int? = null
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActionsByDate(
            groupId = groupId,
            year = year,
            month = month,
            day = day
        )
    }

    suspend fun getGroupActionsByCategory(
        groupId: Int,
        categoryName: String,
        type: Int
    ): Response<List<Action>> {
        return RetrofitInstance.api.getGroupActionsByCategory(
            groupId = groupId,
            categoryName = categoryName,
            type = type
        )
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
            groupId = groupId,
            categoryName = categoryName,
            type = type,
            year = year,
            month = month,
            day = day
        )
    }

    suspend fun addCategory(
        userId: Int,
        token: String,
        categoryName: String,
        type: Int
    ): Response<CategoryResponse> {
        return RetrofitInstance.api.addCategory(
            userId = userId,
            token = token,
            categoryName = categoryName,
            type = type
        )
    }

    suspend fun getUserCategories(userId: Int): Response<List<Category>> {
        return RetrofitInstance.api.getUserCategories(userId)
    }

    suspend fun getUserCategoriesSince(userId: Int, time: String): Response<List<Category>> {
        return RetrofitInstance.api.getUserCategoriesSince(userId, time)
    }

    suspend fun getGroupCategories(groupId: Int): Response<List<Category>> {
        return RetrofitInstance.api.getGroupCategories(groupId)
    }
}
