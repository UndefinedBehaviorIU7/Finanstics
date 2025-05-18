package com.example.finanstics.presentation.addAction

enum class ActionType(val label: String) {
    INCOME("Расход"),
    EXPENSE("Доход"),
    NULL("NULL")
}

fun ActionType.toInt(): Int = when (this) {
    ActionType.INCOME -> 0
    ActionType.EXPENSE -> 1
    ActionType.NULL -> -1
}
