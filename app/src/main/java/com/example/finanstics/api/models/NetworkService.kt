package com.example.assignly.api

import com.example.assignly.models.Action
import com.example.assignly.models.Category
import com.example.assignly.models.CreateActionResponse
import com.example.assignly.models.CreateCategoryResponse
import com.example.assignly.models.User
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {
    @GET("users/{user_id}")
    suspend fun getUser(
        @Path("user_id") userId: Int
    ): Response<User>

    @FormUrlEncoded
    @POST("users/{user_id}/add_category")
    suspend fun addCategory(
        @Path("user_id") userId: Int,
        @Field("token") token: String,
        @Field("category_name") categoryName: String
    ): Response<CreateCategoryResponse>

    @FormUrlEncoded
    @POST("users/{user_id}/add_action")
    suspend fun addAction(
        @Path("user_id") userId: Int,
        @Field("token") token: String,
        @Field("action_name") actionName: String,
        @Field("action_type") actionType: Int,
        @Field("value") value: Int,
        @Field("date") date: String,
        @Field("category_id") categoryId: Int,
        @Field("description") description: String,
        @Field("group_id") groupId: Int?
    ): Response<CreateActionResponse>
}
