package com.ub.finanstics.api.models

import com.google.gson.annotations.SerializedName

@Suppress("ConstructorParameterNaming")
data class Category(
    val id: Int,
    val name: String,
    val type: Int,
    @SerializedName("created_at") val createdAt: String?
)
