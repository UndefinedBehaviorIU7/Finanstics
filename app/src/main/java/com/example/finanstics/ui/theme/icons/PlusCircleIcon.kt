package com.example.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlusCircleIcon: ImageVector
    get() {
        if (_PlusCircle != null) {
            return _PlusCircle!!
        }
        _PlusCircle = ImageVector.Builder(
            name = "PlusCircle",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6f, 10f)
                horizontalLineTo(10f)
                moveTo(10f, 10f)
                horizontalLineTo(14f)
                moveTo(10f, 10f)
                verticalLineTo(14f)
                moveTo(10f, 10f)
                verticalLineTo(6f)
                close()
            }
        }.build()

        return _PlusCircle!!
    }

@Suppress("ObjectPropertyName")
private var _PlusCircle: ImageVector? = null
