package com.example.finanstics.api.models

@Suppress("ConstructorParameterNaming")
data class Action(
    val id: Int,
    val userId: Int,
    val type: Int,
    val name: String,
    val value: Int,
    val date: String,
    val category_id: Int,
    val description: String?,
    val groupId: Int?,
    val created_at: String?
)
