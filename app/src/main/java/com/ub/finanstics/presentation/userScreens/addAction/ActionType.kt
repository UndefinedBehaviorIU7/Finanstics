package com.ub.finanstics.presentation.userScreens.addAction

enum class ActionType(val label: String) {
    INCOME("Доход"),
    EXPENSE("Расход"),
    NULL("NULL")
}

fun ActionType.toInt(): Int = when (this) {
    ActionType.INCOME -> 1
    ActionType.EXPENSE -> 0
    ActionType.NULL -> -1
}
