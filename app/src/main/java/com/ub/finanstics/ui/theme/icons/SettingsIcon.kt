package com.ub.finanstics.ui.theme.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SettingsIcon: ImageVector
    get() {
        if (_Settings != null) {
            return _Settings!!
        }
        _Settings = ImageVector.Builder(
            name = "Settings",
            defaultWidth = 32.dp,
            defaultHeight = 30.dp,
            viewportWidth = 32f,
            viewportHeight = 30f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF090909)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(28.53f, 10.431f)
                lineTo(27.98f, 10.128f)
                curveTo(27.895f, 10.081f, 27.853f, 10.058f, 27.812f, 10.033f)
                curveTo(27.402f, 9.79f, 27.056f, 9.455f, 26.805f, 9.053f)
                curveTo(26.779f, 9.013f, 26.755f, 8.97f, 26.706f, 8.887f)
                curveTo(26.657f, 8.803f, 26.633f, 8.761f, 26.61f, 8.719f)
                curveTo(26.384f, 8.301f, 26.262f, 7.835f, 26.254f, 7.361f)
                curveTo(26.254f, 7.314f, 26.254f, 7.265f, 26.255f, 7.168f)
                lineTo(26.266f, 6.537f)
                curveTo(26.283f, 5.526f, 26.292f, 5.019f, 26.149f, 4.565f)
                curveTo(26.021f, 4.16f, 25.808f, 3.788f, 25.523f, 3.473f)
                curveTo(25.201f, 3.115f, 24.756f, 2.861f, 23.863f, 2.352f)
                lineTo(23.122f, 1.93f)
                curveTo(22.233f, 1.422f, 21.788f, 1.168f, 21.316f, 1.072f)
                curveTo(20.898f, 0.986f, 20.466f, 0.99f, 20.05f, 1.083f)
                curveTo(19.58f, 1.187f, 19.141f, 1.447f, 18.263f, 1.968f)
                lineTo(18.258f, 1.97f)
                lineTo(17.727f, 2.285f)
                curveTo(17.643f, 2.334f, 17.6f, 2.36f, 17.558f, 2.383f)
                curveTo(17.141f, 2.612f, 16.674f, 2.74f, 16.197f, 2.755f)
                curveTo(16.149f, 2.756f, 16.1f, 2.756f, 16.002f, 2.756f)
                curveTo(15.905f, 2.756f, 15.854f, 2.756f, 15.806f, 2.755f)
                curveTo(15.327f, 2.739f, 14.86f, 2.612f, 14.442f, 2.381f)
                curveTo(14.399f, 2.358f, 14.358f, 2.332f, 14.274f, 2.283f)
                lineTo(13.739f, 1.965f)
                curveTo(12.855f, 1.44f, 12.412f, 1.177f, 11.94f, 1.072f)
                curveTo(11.522f, 0.979f, 11.089f, 0.976f, 10.67f, 1.063f)
                curveTo(10.196f, 1.161f, 9.751f, 1.417f, 8.861f, 1.928f)
                lineTo(8.857f, 1.93f)
                lineTo(8.125f, 2.35f)
                lineTo(8.117f, 2.355f)
                curveTo(7.235f, 2.861f, 6.793f, 3.115f, 6.474f, 3.471f)
                curveTo(6.191f, 3.786f, 5.979f, 4.158f, 5.852f, 4.561f)
                curveTo(5.709f, 5.016f, 5.717f, 5.524f, 5.734f, 6.54f)
                lineTo(5.745f, 7.17f)
                curveTo(5.746f, 7.266f, 5.749f, 7.313f, 5.748f, 7.36f)
                curveTo(5.741f, 7.835f, 5.617f, 8.302f, 5.39f, 8.72f)
                curveTo(5.368f, 8.761f, 5.344f, 8.803f, 5.296f, 8.885f)
                curveTo(5.247f, 8.968f, 5.224f, 9.009f, 5.199f, 9.049f)
                curveTo(4.946f, 9.453f, 4.599f, 9.791f, 4.186f, 10.034f)
                curveTo(4.146f, 10.058f, 4.103f, 10.081f, 4.018f, 10.127f)
                lineTo(3.476f, 10.425f)
                curveTo(2.573f, 10.92f, 2.122f, 11.168f, 1.793f, 11.52f)
                curveTo(1.503f, 11.832f, 1.283f, 12.202f, 1.149f, 12.605f)
                curveTo(0.998f, 13.061f, 0.998f, 13.571f, 1f, 14.593f)
                lineTo(1.002f, 15.427f)
                curveTo(1.005f, 16.442f, 1.008f, 16.949f, 1.16f, 17.401f)
                curveTo(1.294f, 17.801f, 1.512f, 18.169f, 1.801f, 18.479f)
                curveTo(2.127f, 18.83f, 2.574f, 19.076f, 3.47f, 19.569f)
                lineTo(4.007f, 19.865f)
                curveTo(4.099f, 19.915f, 4.145f, 19.94f, 4.189f, 19.966f)
                curveTo(4.598f, 20.21f, 4.942f, 20.547f, 5.193f, 20.948f)
                curveTo(5.22f, 20.991f, 5.246f, 21.036f, 5.298f, 21.126f)
                curveTo(5.35f, 21.215f, 5.376f, 21.26f, 5.4f, 21.304f)
                curveTo(5.62f, 21.717f, 5.738f, 22.175f, 5.746f, 22.641f)
                curveTo(5.747f, 22.692f, 5.746f, 22.743f, 5.744f, 22.845f)
                lineTo(5.734f, 23.45f)
                curveTo(5.716f, 24.469f, 5.709f, 24.979f, 5.853f, 25.436f)
                curveTo(5.98f, 25.84f, 6.193f, 26.212f, 6.478f, 26.528f)
                curveTo(6.8f, 26.885f, 7.246f, 27.139f, 8.139f, 27.648f)
                lineTo(8.879f, 28.07f)
                curveTo(9.769f, 28.578f, 10.214f, 28.831f, 10.686f, 28.928f)
                curveTo(11.104f, 29.014f, 11.535f, 29.01f, 11.951f, 28.918f)
                curveTo(12.422f, 28.813f, 12.863f, 28.552f, 13.744f, 28.03f)
                lineTo(14.275f, 27.715f)
                curveTo(14.359f, 27.666f, 14.401f, 27.641f, 14.443f, 27.618f)
                curveTo(14.861f, 27.388f, 15.327f, 27.26f, 15.804f, 27.245f)
                curveTo(15.852f, 27.243f, 15.901f, 27.243f, 15.999f, 27.243f)
                curveTo(16.097f, 27.243f, 16.146f, 27.243f, 16.194f, 27.245f)
                curveTo(16.673f, 27.26f, 17.141f, 27.388f, 17.56f, 27.619f)
                curveTo(17.597f, 27.639f, 17.633f, 27.661f, 17.698f, 27.7f)
                lineTo(18.263f, 28.035f)
                curveTo(19.147f, 28.56f, 19.589f, 28.823f, 20.061f, 28.928f)
                curveTo(20.479f, 29.02f, 20.912f, 29.024f, 21.332f, 28.938f)
                curveTo(21.805f, 28.84f, 22.251f, 28.584f, 23.141f, 28.073f)
                lineTo(23.883f, 27.646f)
                curveTo(24.766f, 27.139f, 25.209f, 26.885f, 25.528f, 26.529f)
                curveTo(25.811f, 26.214f, 26.023f, 25.842f, 26.15f, 25.439f)
                curveTo(26.292f, 24.987f, 26.284f, 24.483f, 26.267f, 23.482f)
                lineTo(26.255f, 22.83f)
                curveTo(26.254f, 22.734f, 26.254f, 22.687f, 26.254f, 22.64f)
                curveTo(26.261f, 22.165f, 26.383f, 21.698f, 26.61f, 21.28f)
                curveTo(26.632f, 21.238f, 26.657f, 21.197f, 26.705f, 21.114f)
                curveTo(26.753f, 21.031f, 26.778f, 20.99f, 26.803f, 20.95f)
                curveTo(27.056f, 20.546f, 27.404f, 20.209f, 27.816f, 19.965f)
                curveTo(27.856f, 19.941f, 27.898f, 19.919f, 27.98f, 19.874f)
                lineTo(27.983f, 19.872f)
                lineTo(28.526f, 19.575f)
                curveTo(29.428f, 19.08f, 29.881f, 18.832f, 30.209f, 18.479f)
                curveTo(30.499f, 18.167f, 30.719f, 17.798f, 30.852f, 17.395f)
                curveTo(31.003f, 16.942f, 31.002f, 16.434f, 31f, 15.425f)
                lineTo(30.997f, 14.572f)
                curveTo(30.995f, 13.558f, 30.994f, 13.051f, 30.842f, 12.599f)
                curveTo(30.708f, 12.198f, 30.489f, 11.831f, 30.2f, 11.521f)
                curveTo(29.874f, 11.17f, 29.427f, 10.924f, 28.532f, 10.432f)
                lineTo(28.53f, 10.431f)
                close()
            }
            path(
                stroke = SolidColor(Color(0xFF090909)),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.998f, 15f)
                curveTo(9.998f, 18.281f, 12.686f, 20.94f, 16f, 20.94f)
                curveTo(19.316f, 20.94f, 22.003f, 18.281f, 22.003f, 15f)
                curveTo(22.003f, 11.719f, 19.316f, 9.06f, 16f, 9.06f)
                curveTo(12.686f, 9.06f, 9.998f, 11.719f, 9.998f, 15f)
                close()
            }
        }.build()

        return _Settings!!
    }

@Suppress("ObjectPropertyName")
private var _Settings: ImageVector? = null
