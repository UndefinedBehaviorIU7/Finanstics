package com.ub.finanstics.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

val Blue = Color(0xFF2473F1)
val LightBlue = Color(0xFF2DB8F6)
val LightGreen = Color(0xFF7EF698)
val Green = Color(0xFF42BA5E)

val ColorsIncomes = mutableListOf(
    Blue,
    Green,
    LightGreen,
    LightBlue
)

val Yellow = Color(0xFFFCC427)
val Orange = Color(0xFFFF9800)
val Red = Color(0xFFC62D2D)
val Purple = Color(0xFF912EFA)
val Pink = Color(0xFFF149E9)

val ColorsExpenses = mutableListOf(
    Red,
    Yellow,
    Orange,
    Purple,
    Pink
)

val GreyDark = Color(0xFF303030)
val GreyLight = Color(0xFFB9B9B9)

val WhiteSoft = Color(0xFFF7F7F7)
val BlackSoft = Color(0xFF121212)

val YellowSoftLight = Color(0xFFFFC934)
val YellowSoftDark = Color(0xFFFEE06B)

val BackLight = Color(0xFFEAEAEA)
val BackDark = Color(0xFF212121)

val Background2 = Color(0xFF171717)

fun generateWarmColor(): Color {
    val red = Random.nextInt(200, 255)
    val green = Random.nextInt(50, 200)
    val blue = Random.nextInt(0, 120)

    return Color(red, green, blue)
}

fun generateColdColor(): Color {
    val red = Random.nextInt(0, 100)
    val green = Random.nextInt(100, 200)
    val blue = Random.nextInt(200, 255)

    return Color(red, green, blue)
}

fun averageColor(colors: List<Color>): Color {
    if (colors.isEmpty()) return Color.Transparent

    var totalAlpha = 0f
    var totalRed = 0f
    var totalGreen = 0f
    var totalBlue = 0f

    for (color in colors) {
        totalAlpha += color.alpha
        totalRed += color.red
        totalGreen += color.green
        totalBlue += color.blue
    }

    val count = colors.size
    return Color(
        alpha = totalAlpha / count,
        red = totalRed / count,
        green = totalGreen / count,
        blue = totalBlue / count
    )
}