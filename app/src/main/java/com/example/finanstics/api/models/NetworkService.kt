package com.example.finanstics.api.models

import com.example.finanstics.db.Category
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {
    @GET("users/{user_id}")
    suspend fun getUser(
        @Path("user_id") userId: Int
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
        @Query("group_id") groupId: Int?
    ): Response<ActionResponse>

    @GET("users/{user_id}/actions")
    suspend fun getUserActions(
        @Path("user_id") userId: Int
    ): Response<List<Action>>

    @GET("users/{user_id}/categories")
    suspend fun getUserCategories(
        @Path("user_id") userId: Int
    ): Response<List<Category>>
}
