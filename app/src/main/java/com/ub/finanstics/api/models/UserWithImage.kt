package com.ub.finanstics.api.models

import coil3.Bitmap

data class UserWithImage(
    val user: User,
    val image: Bitmap?
)