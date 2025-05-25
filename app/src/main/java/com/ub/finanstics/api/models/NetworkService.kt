package com.ub.finanstics.api.models

import com.ub.finanstics.api.responses.ActionResponse
import com.ub.finanstics.api.responses.BaseResponse
import com.ub.finanstics.api.responses.CategoryResponse
import com.ub.finanstics.api.responses.UserResponse
import com.ub.finanstics.api.responses.VKUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

@Suppress("TooManyFunctions")
interface NetworkService {
    @GET("users/{user_id}")
    suspend fun getUser(
        @Path("user_id") userId: Int
    ): Response<User>

    @GET("users/tags/{tag}")
    suspend fun getUserByTag(
        @Path("tag") tag: String
    ): Response<User>

    @GET("users/vk/{vk_id}")
    suspend fun getUserVK(
        @Path("vk_id") vkId: Int
    ): Response<User>

    @POST("users/{user_id}/add_category")
    suspend fun addCategory(
        @Path("user_id") userId: Int,
        @Query("token") token: String,
        @Query("category_name") categoryName: String,
        @Query("type") type: Int,
    ): Response<CategoryResponse>

    @Suppress("LongParameterList")
    @POST("users/{user_id}/add_action")
    suspend fun addAction(
        @Path("user_id") userId: Int,
        @Query("token") token: String,
        @Query("action_name") actionName: String,
        @Query("action_type") actionType: Int,
        @Query("value") value: Int,
        @Query("date_str") date: String,
        @Query("category_id") categoryId: Int,
        @Query("description") description: String?,
        @Query("groups_ids") groupId: List<Int>?
    ): Response<ActionResponse>

    @GET("users/{user_id}/actions/all")
    suspend fun getUserActions(
        @Path("user_id") userId: Int
    ): Response<List<Action>>

    @GET("users/{user_id}/actions/{time}")
    suspend fun getUserActionsSince(
        @Path("user_id") userId: Int,
        @Path("time") time: String
    ): Response<List<Action>>

    @GET("users/{user_id}/categories/all")
    suspend fun getUserCategories(
        @Path("user_id") userId: Int
    ): Response<List<Category>>

    @GET("users/{user_id}/categories/{time}")
    suspend fun getUserCategoriesSince(
        @Path("user_id") userId: Int,
        @Path("time") time: String
    ): Response<List<Category>>

    @POST("register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("tag") tag: String,
    ): Response<UserResponse>

    @Suppress("LongParameterList")
    @POST("register/vk")
    suspend fun registerVK(
        @Query("vk_id") vkId: Int,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("tag") tag: String,
    ): Response<UserResponse>

    @GET("login")
    suspend fun login(
        @Query("tag") tag: String,
        @Query("password") password: String,
    ): Response<UserResponse>

    @GET("login/vk")
    suspend fun loginVK(
        @Query("vk_id") vkId: Int
    ): Response<VKUserResponse>

    @GET("groups/{group_id}/actions")
    suspend fun getGroupActionsByDate(
        @Path("group_id") groupId: Int,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int?,
    ): Response<List<Action>>

    @GET("groups/{group_id}/actions/all")
    suspend fun getGroupActions(
        @Path("group_id") groupId: Int
    ): Response<List<Action>>

    @GET("groups/{group_id}")
    suspend fun getGroupById(
        @Path("group_id") groupId: Int
    ): Response<Group>

    @GET("groups/all")
    suspend fun getAllGroups(): Response<List<Group>>

    @GET("/users/{user_id}/groups")
    suspend fun getUserGroups(
        @Path("user_id") userId: Int
    ): Response<List<Group>>

    @POST("/users/register_fcm_token")
    suspend fun registerFCMToken(
        @Query("token") token: String,
        @Query("fcm_token") fcmToken: String
    ): Response<ResponseBody>

    @POST("/logout")
    suspend fun logout(
        @Query("token") token: String
    ): Response<BaseResponse>

    @GET("/users/{user_id}/image")
    @Streaming
    suspend fun getUserImage(
        @Path("user_id") userId: Int
    ): Response<ResponseBody>

    @GET("/users/{group_id}/image")
    @Streaming
    suspend fun getGroupImage(
        @Path("group_id") userId: Int
    ): Response<ResponseBody>

    @GET("/user_info")
    suspend fun userInfo(
        @Query("user_id") userId: Int
    ): Response<User>

    @POST("/users/{user_id}/update_data")
    @Streaming
    suspend fun updateUserData(
        @Path("user_id") userId: Int,
        @Query("token") token: String,
        @Query("user_data") userData: String
    ): Response<BaseResponse>

    @POST("/users/{user_id}/update_image")
    @Multipart
    @Streaming
    suspend fun updateUserImage(
        @Path("user_id") userId: String,
        @Part("token") token: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<BaseResponse>

    @POST("/groups/create")
    @Multipart
    suspend fun createGroup(
        @Part("token") token: RequestBody,
        @Part("group_name") groupName: RequestBody,
        @Part("group_data") groupData: RequestBody,
        @Part("users") users: RequestBody
    ): Response<BaseResponse>
}
