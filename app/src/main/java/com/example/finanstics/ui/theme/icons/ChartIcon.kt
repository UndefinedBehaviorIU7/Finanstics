package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChartIcon: ImageVector
    get() {
        if (_Chart != null) {
            return _Chart!!
        }
        _Chart = ImageVector.Builder(
            name = "Chart",
            defaultWidth = 33.dp,
            defaultHeight = 33.dp,
            viewportWidth = 33f,
            viewportHeight = 33f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF090909)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.667f, 1.5f)
                curveTo(8.382f, 1.5f, 1.667f, 8.216f, 1.667f, 16.5f)
                curveTo(1.667f, 24.784f, 8.382f, 31.5f, 16.667f, 31.5f)
                curveTo(24.951f, 31.5f, 31.667f, 24.784f, 31.667f, 16.5f)
                moveTo(16.667f, 1.5f)
                curveTo(24.951f, 1.5f, 31.667f, 8.216f, 31.667f, 16.5f)
                moveTo(16.667f, 1.5f)
                verticalLineTo(16.5f)
                moveTo(31.667f, 16.5f)
                horizontalLineTo(16.667f)
                moveTo(26.667f, 27.333f)
                lineTo(16.667f, 16.5f)
            }
        }.build()

        return _Chart!!
    }

@Suppress("ObjectPropertyName")
private var _Chart: ImageVector? = null
