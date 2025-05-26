package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MinimizeButton: ImageVector
    get() {
        if (_MinimizeButton != null) {
            return _MinimizeButton!!
        }
        _MinimizeButton = ImageVector.Builder(
            name = "MinimizeButton",
            defaultWidth = 1300.dp,
            defaultHeight = 196.dp,
            viewportWidth = 1300f,
            viewportHeight = 196f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(350.5f, 8.5f)
                curveToRelative(-20.2f, 2.2f, -41.1f, 11.1f, -55.7f, 23.7f)
                curveToRelative(-6.8f, 5.9f, -11.7f, 11.8f, -26.3f, 31.7f)
                curveToRelative(-15.8f, 21.4f, -20.5f, 26.9f, -38.4f, 45.3f)
                curveToRelative(-23.3f, 23.8f, -37.1f, 35.1f, -58.1f, 47.3f)
                curveToRelative(-36.8f, 21.5f, -80.5f, 36f, -115.5f, 38.5f)
                curveToRelative(-6.7f, 0.5f, 2.1f, 0.7f, 26.4f, 0.8f)
                curveToRelative(42.5f, 0.2f, 40.2f, 0.6f, 65.7f, -11.1f)
                curveToRelative(37.5f, -17.2f, 60.2f, -33.5f, 91.4f, -65.4f)
                curveToRelative(18f, -18.5f, 22.7f, -24f, 38.5f, -45.4f)
                curveToRelative(20.4f, -27.8f, 26.5f, -33.9f, 41f, -41.1f)
                curveToRelative(12.4f, -6.2f, 23.2f, -9.5f, 35.3f, -10.7f)
                curveToRelative(14.5f, -1.5f, 575.4f, -1.5f, 590.7f, -0f)
                curveToRelative(12.8f, 1.2f, 23.3f, 4.4f, 36f, 10.7f)
                curveToRelative(13.9f, 6.9f, 20.8f, 13.7f, 40.2f, 40f)
                curveToRelative(16.3f, 22.1f, 34.9f, 43.8f, 51f, 59.3f)
                curveToRelative(19.5f, 19f, 49.6f, 38.8f, 79.7f, 52.6f)
                curveToRelative(25.7f, 11.8f, 22.9f, 11.3f, 65f, 11.1f)
                curveToRelative(24.9f, -0.1f, 33f, -0.3f, 26.1f, -0.8f)
                curveToRelative(-24.7f, -1.9f, -58.5f, -11.1f, -85.1f, -23.3f)
                curveToRelative(-51.8f, -23.8f, -85f, -52.3f, -126.8f, -109f)
                curveToRelative(-13.5f, -18.3f, -18.6f, -24.4f, -25.5f, -30.4f)
                curveToRelative(-9.5f, -8.3f, -21f, -14.7f, -35.1f, -19.5f)
                curveToRelative(-17.7f, -6f, -7.7f, -5.8f, -322.9f, -5.7f)
                curveToRelative(-215.1f, 0.1f, -289.3f, 0.5f, -297.6f, 1.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(505.7f, 85.7f)
                curveToRelative(-2.4f, 2.8f, -2.2f, 7.5f, 0.6f, 10f)
                curveToRelative(1.2f, 1.1f, 33.3f, 22.9f, 71.2f, 48.4f)
                curveToRelative(62.4f, 42f, 69.4f, 46.4f, 73f, 46.4f)
                curveToRelative(3.6f, -0f, 10.6f, -4.4f, 73f, -46.4f)
                curveToRelative(38f, -25.5f, 70f, -47.3f, 71.3f, -48.4f)
                curveToRelative(4.9f, -4.5f, 1.6f, -12.3f, -5f, -11.5f)
                curveToRelative(-2.3f, 0.2f, -23.6f, 14f, -70.8f, 45.8f)
                curveToRelative(-37.1f, 24.9f, -67.9f, 45.3f, -68.5f, 45.3f)
                curveToRelative(-0.6f, -0f, -31.4f, -20.4f, -68.5f, -45.4f)
                curveToRelative(-71f, -47.7f, -72.4f, -48.5f, -76.3f, -44.2f)
                close()
            }
        }.build()

        return _MinimizeButton!!
    }

@Suppress("ObjectPropertyName")
private var _MinimizeButton: ImageVector? = null
