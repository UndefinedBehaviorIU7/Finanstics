package com.example.finanstics.api.models

data class Action(
    val id: Int,
    val userId: Int?,
    val type: Int?,
    val name: String?,
    val value: Int?,
    val date: String?,
    val categoryId: Int?,
    val description: String?,
    val groupId: Int?,
    val createdAt: String?
)
