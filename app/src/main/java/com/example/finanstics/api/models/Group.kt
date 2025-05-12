package com.example.finanstics.api.models

@Suppress("ConstructorParameterNaming")
data class Group(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val groupData: String?,
    val image: String?,
    val createdAt: String?,
    val users: List<Int>?,
    val admins: List<Int>?
)