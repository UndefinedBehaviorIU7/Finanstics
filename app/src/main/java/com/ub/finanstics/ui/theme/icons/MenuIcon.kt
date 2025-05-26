package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MenuIcon: ImageVector
    get() {
        if (_HamburgerMD != null) {
            return _HamburgerMD!!
        }
        _HamburgerMD = ImageVector.Builder(
            name = "HamburgerMD",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5f, 17f)
                horizontalLineTo(19f)
                moveTo(5f, 12f)
                horizontalLineTo(19f)
                moveTo(5f, 7f)
                horizontalLineTo(19f)
            }
        }.build()

        return _HamburgerMD!!
    }

@Suppress("ObjectPropertyName")
private var _HamburgerMD: ImageVector? = null
