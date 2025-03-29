package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PersonIcon: ImageVector
    get() {
        if (_Person != null) {
            return _Person!!
        }
        _Person = ImageVector.Builder(
            name = "Person",
            defaultWidth = 14.dp,
            defaultHeight = 17.dp,
            viewportWidth = 14f,
            viewportHeight = 17f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(13f, 16f)
                curveTo(13f, 13.791f, 10.314f, 12f, 7f, 12f)
                curveTo(3.686f, 12f, 1f, 13.791f, 1f, 16f)
                moveTo(7f, 9f)
                curveTo(4.791f, 9f, 3f, 7.209f, 3f, 5f)
                curveTo(3f, 2.791f, 4.791f, 1f, 7f, 1f)
                curveTo(9.209f, 1f, 11f, 2.791f, 11f, 5f)
                curveTo(11f, 7.209f, 9.209f, 9f, 7f, 9f)
                close()
            }
        }.build()

        return _Person!!
    }

@Suppress("ObjectPropertyName")
private var _Person: ImageVector? = null
