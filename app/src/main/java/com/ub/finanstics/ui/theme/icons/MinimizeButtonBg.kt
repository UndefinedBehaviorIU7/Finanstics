package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MinimizeButtonBg: ImageVector
    get() {
        if (_MinimizeButtonBg != null) {
            return _MinimizeButtonBg!!
        }
        _MinimizeButtonBg = ImageVector.Builder(
            name = "MinimizeButtonBg",
            defaultWidth = 1300.dp,
            defaultHeight = 196.dp,
            viewportWidth = 1300f,
            viewportHeight = 196f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(350.5f, 8.5f)
                curveToRelative(-20.2f, 2.2f, -41.1f, 11.1f, -55.7f, 23.7f)
                curveToRelative(-6.8f, 5.9f, -11.7f, 11.8f, -26.3f, 31.7f)
                curveToRelative(-15.8f, 21.4f, -20.5f, 26.9f, -38.4f, 45.3f)
                curveToRelative(-23.3f, 23.8f, -37.1f, 35.1f, -58.1f, 47.3f)
                curveToRelative(-36.8f, 21.5f, -80.5f, 36f, -115.5f, 38.5f)
                curveToRelative(-5.5f, 0.4f, 261.6f, 0.7f, 593.5f, 0.7f)
                curveToRelative(331.9f, -0f, 599f, -0.3f, 593.5f, -0.7f)
                curveToRelative(-24.7f, -1.9f, -58.5f, -11.1f, -85.1f, -23.3f)
                curveToRelative(-51.8f, -23.8f, -85f, -52.3f, -126.8f, -109f)
                curveToRelative(-13.5f, -18.3f, -18.6f, -24.4f, -25.5f, -30.4f)
                curveToRelative(-9.5f, -8.3f, -21f, -14.7f, -35.1f, -19.5f)
                curveToRelative(-17.7f, -6f, -7.7f, -5.8f, -322.9f, -5.7f)
                curveToRelative(-215.1f, 0.1f, -289.3f, 0.5f, -297.6f, 1.4f)
                close()
            }
        }.build()

        return _MinimizeButtonBg!!
    }

@Suppress("ObjectPropertyName")
private var _MinimizeButtonBg: ImageVector? = null
