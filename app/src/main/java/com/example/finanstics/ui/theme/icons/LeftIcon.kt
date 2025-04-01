package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LeftIcon: ImageVector
    get() {
        if (_LeftIcon != null) {
            return _LeftIcon!!
        }
        _LeftIcon = ImageVector.Builder(
            name = "LeftIcon",
            defaultWidth = 9.dp,
            defaultHeight = 16.dp,
            viewportWidth = 9f,
            viewportHeight = 16f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 15f)
                lineTo(1f, 8f)
                lineTo(8f, 1f)
            }
        }.build()

        return _LeftIcon!!
    }

@Suppress("ObjectPropertyName")
private var _LeftIcon: ImageVector? = null
