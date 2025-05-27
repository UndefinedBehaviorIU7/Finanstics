package com.ub.finanstics.api.models

import com.google.gson.annotations.SerializedName

@Suppress("ConstructorParameterNaming")
data class Group(
    val id: Int,
    @SerializedName("owner_id") val ownerId: Int,
    val name: String,
    @SerializedName("group_data") val groupData: String?,
    val image: String?,
    @SerializedName("created_at") val createdAt: String?,
    val users: List<Int>?,
    val admins: List<Int>?
)
