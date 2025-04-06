package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val UpIcon: ImageVector
    get() {
        if (_UpIcon != null) {
            return _UpIcon!!
        }
        _UpIcon = ImageVector.Builder(
            name = "UpIcon",
            defaultWidth = 30.dp,
            defaultHeight = 9.dp,
            viewportWidth = 16f,
            viewportHeight = 9f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(1f, 8f)
                lineTo(8f, 1f)
                lineTo(15f, 8f)
            }
        }.build()

        return _UpIcon!!
    }

@Suppress("ObjectPropertyName")
private var _UpIcon: ImageVector? = null
