package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Circle: ImageVector
    get() {
        if (_Circle != null) {
            return _Circle!!
        }
        _Circle = ImageVector.Builder(
            name = "Circle",
            defaultWidth = 40.dp,
            defaultHeight = 38.dp,
            viewportWidth = 40f,
            viewportHeight = 38f
        ).apply {
            path(fill = SolidColor(Color(0xFFD9D9D9))) {
                moveTo(0f, 19f)
                arcToRelative(19.965f, 19f, 0f, isMoreThanHalf = true, isPositiveArc = false, 39.929f, 0f)
                arcToRelative(19.965f, 19f, 0f, isMoreThanHalf = true, isPositiveArc = false, -39.929f, 0f)
                close()
            }
        }.build()

        return _Circle!!
    }

@Suppress("ObjectPropertyName")
private var _Circle: ImageVector? = null
