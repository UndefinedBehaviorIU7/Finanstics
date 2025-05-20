package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CalendarIcon: ImageVector
    get() {
        if (_Calendar != null) {
            return _Calendar!!
        }
        _Calendar = ImageVector.Builder(
            name = "Calendar",
            defaultWidth = 26.dp,
            defaultHeight = 29.dp,
            viewportWidth = 26f,
            viewportHeight = 29f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF090909)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(1.167f, 10.167f)
                horizontalLineTo(24.5f)
                moveTo(1.167f, 10.167f)
                verticalLineTo(23f)
                curveTo(1.167f, 24.634f, 1.167f, 25.45f, 1.485f, 26.074f)
                curveTo(1.764f, 26.623f, 2.21f, 27.069f, 2.759f, 27.349f)
                curveTo(3.382f, 27.667f, 4.199f, 27.667f, 5.829f, 27.667f)
                horizontalLineTo(19.838f)
                curveTo(21.468f, 27.667f, 22.283f, 27.667f, 22.907f, 27.349f)
                curveTo(23.455f, 27.069f, 23.903f, 26.623f, 24.182f, 26.074f)
                curveTo(24.5f, 25.451f, 24.5f, 24.635f, 24.5f, 23.005f)
                verticalLineTo(10.167f)
                moveTo(1.167f, 10.167f)
                verticalLineTo(9f)
                curveTo(1.167f, 7.367f, 1.167f, 6.549f, 1.485f, 5.926f)
                curveTo(1.764f, 5.377f, 2.21f, 4.931f, 2.759f, 4.651f)
                curveTo(3.383f, 4.333f, 4.2f, 4.333f, 5.834f, 4.333f)
                horizontalLineTo(7f)
                moveTo(24.5f, 10.167f)
                verticalLineTo(8.996f)
                curveTo(24.5f, 7.365f, 24.5f, 6.549f, 24.182f, 5.926f)
                curveTo(23.903f, 5.377f, 23.455f, 4.931f, 22.907f, 4.651f)
                curveTo(22.283f, 4.333f, 21.467f, 4.333f, 19.834f, 4.333f)
                horizontalLineTo(18.667f)
                moveTo(18.667f, 1.417f)
                verticalLineTo(4.333f)
                moveTo(18.667f, 4.333f)
                horizontalLineTo(7f)
                moveTo(7f, 1.417f)
                verticalLineTo(4.333f)
            }
        }.build()

        return _Calendar!!
    }

@Suppress("ObjectPropertyName")
private var _Calendar: ImageVector? = null
