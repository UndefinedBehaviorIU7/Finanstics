package com.example.assignly.models

data class User(
    val id: Int,
    val tag: String?,
    val balance: Int?,
    val password: String?,
    val username: String?,
    val image: String?,
    val user_data: String?,
    val created_at: String?,
    val groups: List<Int>?,
    val user_to_categories: List<Int>?
)
