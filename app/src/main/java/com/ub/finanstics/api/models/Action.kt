package com.ub.finanstics.api.models

import com.google.gson.annotations.SerializedName

@Suppress("ConstructorParameterNaming")
data class Action(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val type: Int,
    val name: String,
    val value: Int,
    val date: String,
    @SerializedName("category_id") val categoryId: Int,
    val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    val groups: List<Int>
)
