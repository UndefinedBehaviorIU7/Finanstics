package com.ub.finanstics.api.responses

import com.google.gson.annotations.SerializedName

@Suppress("ConstructorParameterNaming")
data class CategoryResponse(
    val id: Int,
    @SerializedName("created_at") val createdAt: String
)
