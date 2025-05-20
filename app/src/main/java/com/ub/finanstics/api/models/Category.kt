package com.ub.finanstics.api.models

@Suppress("ConstructorParameterNaming")
data class Category(
    val id: Int,
    val name: String,
    val type: Int,
    val created_at: String?
)
