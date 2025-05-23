package com.ub.finanstics.api.models

import com.google.gson.annotations.SerializedName

@Suppress("ConstructorParameterNaming")
data class User(
    val id: Int,
    val tag: String?,
    val balance: Int?,
    val password: String?,
    val username: String?,
    val image: String?,

    @SerializedName("user_data")
    val userData: String?,

    val createdAt: String?,
    val groups: List<Int>?,
    val vk_id: Int?,
    val userToCategories: List<Int>?
)
