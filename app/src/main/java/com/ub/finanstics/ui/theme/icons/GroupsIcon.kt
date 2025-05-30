package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GroupsIcon: ImageVector
    get() {
        if (_Groups != null) {
            return _Groups!!
        }
        _Groups = ImageVector.Builder(
            name = "Groups",
            defaultWidth = 32.dp,
            defaultHeight = 27.dp,
            viewportWidth = 32f,
            viewportHeight = 27f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 2.7f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(24.333f, 26f)
                curveTo(24.333f, 23.239f, 20.602f, 21f, 16f, 21f)
                curveTo(11.398f, 21f, 7.667f, 23.239f, 7.667f, 26f)
                moveTo(31f, 21.001f)
                curveTo(31f, 18.95f, 28.943f, 17.188f, 26f, 16.417f)
                moveTo(1f, 21.001f)
                curveTo(1f, 18.95f, 3.057f, 17.188f, 6f, 16.417f)
                moveTo(26f, 9.727f)
                curveTo(27.023f, 8.811f, 27.667f, 7.481f, 27.667f, 6f)
                curveTo(27.667f, 3.239f, 25.428f, 1f, 22.667f, 1f)
                curveTo(21.386f, 1f, 20.218f, 1.481f, 19.333f, 2.273f)
                moveTo(6f, 9.727f)
                curveTo(4.977f, 8.811f, 4.333f, 7.481f, 4.333f, 6f)
                curveTo(4.333f, 3.239f, 6.572f, 1f, 9.333f, 1f)
                curveTo(10.614f, 1f, 11.782f, 1.481f, 12.667f, 2.273f)
                moveTo(16f, 16f)
                curveTo(13.239f, 16f, 11f, 13.761f, 11f, 11f)
                curveTo(11f, 8.239f, 13.239f, 6f, 16f, 6f)
                curveTo(18.761f, 6f, 21f, 8.239f, 21f, 11f)
                curveTo(21f, 13.761f, 18.761f, 16f, 16f, 16f)
                close()
            }
        }.build()

        return _Groups!!
    }

@Suppress("ObjectPropertyName")
private var _Groups: ImageVector? = null
