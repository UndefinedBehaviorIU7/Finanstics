package com.ub.finanstics.api.models

import coil3.Bitmap

data class GroupWithImage(
    val group: Group,
    val image: Bitmap?
)