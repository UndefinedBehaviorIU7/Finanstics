package com.example.finanstics.api.models

data class User(
    val id: Int,
    val tag: String?,
    val balance: Int?,
    val password: String?,
    val username: String?,
    val image: String?,
    val userData: String?,
    val createdAt: String?,
    val groups: List<Int>?,
    val userToCategories: List<Int>?
)
