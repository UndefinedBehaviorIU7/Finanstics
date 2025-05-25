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
    val category_id: Int,
    val description: String?,
    val created_at: String?,
    val groups: List<Int>
)
