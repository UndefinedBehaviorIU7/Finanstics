package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DownIcon: ImageVector
    get() {
        if (_DownIcon != null) {
            return _DownIcon!!
        }
        _DownIcon = ImageVector.Builder(
            name = "DownIcon",
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
                moveTo(15f, 1f)
                lineTo(8f, 8f)
                lineTo(1f, 1f)
            }
        }.build()

        return _DownIcon!!
    }

@Suppress("ObjectPropertyName")
private var _DownIcon: ImageVector? = null
