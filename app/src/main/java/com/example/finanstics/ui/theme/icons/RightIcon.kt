package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val RightIcon: ImageVector
    get() {
        if (_RightIcon != null) {
            return _RightIcon!!
        }
        _RightIcon = ImageVector.Builder(
            name = "RightIcon",
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
                moveTo(1f, 1f)
                lineTo(8f, 8f)
                lineTo(1f, 15f)
            }
        }.build()

        return _RightIcon!!
    }

@Suppress("ObjectPropertyName")
private var _RightIcon: ImageVector? = null
