package com.ub.finanstics.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private const val PURPLE_LIGHT_HEX = 0xFFD0BCFF
private const val PURPLE_GREY_LIGHT_HEX = 0xFFCCC2DC
private const val PINK_LIGHT_HEX = 0xFFEFB8C8

private const val PURPLE_DARK_HEX = 0xFF6650a4
private const val PURPLE_GREY_DARK_HEX = 0xFF625b71
private const val PINK_DARK_HEX = 0xFF7D5260

val PurpleLight = Color(PURPLE_LIGHT_HEX)
val PurpleGreyLight = Color(PURPLE_GREY_LIGHT_HEX)
val PinkLight = Color(PINK_LIGHT_HEX)

val PurpleDark = Color(PURPLE_DARK_HEX)
val PurpleGreyDark = Color(PURPLE_GREY_DARK_HEX)
val PinkDark = Color(PINK_DARK_HEX)

val Blue = Color(0xFF2171F3)
val LightBlue = Color(0xFF2DB8F6)
val LightGreen = Color(0xFF7EF698)
val Green = Color(0xFF42BA5E)

val ColorsIncomes = mutableListOf(
    Blue,
    Green,
    LightGreen,
    LightBlue
)

val Yellow = Color(0xFFFFEB3B)
val Orange = Color(0xFFFF9800)
val Red = Color(0xFFA82828)
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
