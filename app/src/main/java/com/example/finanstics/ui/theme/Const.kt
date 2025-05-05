package com.example.finanstics.ui.theme

import androidx.compose.ui.unit.dp

const val EXPENSES = "Расходы"
const val INCOMES = "Доходы"
const val DEGREES_MAX = 360
val OFFSET_BAR = 60.dp
const val ALL_TIME = "всё время"
const val MIN_CATEGORIES_SIZE = 17

const val PREF_NAME = "FinansticsPref"

const val TIME_INIT = "2025-01-01T00:00:01.230233"

const val USER_ID = 2
const val TOKEN = "74ea432d53847930fb534a9f4ce059054a98e6ddeb2e2220a5450d9706cfbb64"
const val GROUP_ID = 1

val INCOMES_DATA = listOf(
    "Зарплата" to 10000,
    "Стипендия" to 5500,
    "Переводы" to 2000,
)
val INCOMES_DATA1 = listOf(
    "Зарплата" to 20000,
    "Переводы" to 1000,
)

val EXPENSES_DATA = listOf(
    "Покупки" to 16500,
    "Налоги/штрафы" to 7400,
    "Еда" to 4000,
    "Развлечения" to 2200,
    "Транспорт" to 1900,
    "Здоровье" to 2500
)

val EXPENSES_DATA1 = listOf(
    "Покупки" to 500,
    "Налоги/штрафы" to 700,
    "Еда" to 2000,
    "Транспорт" to 100
)

val INCOMES_DATA_MONTHS = listOf(
    INCOMES_DATA,
    INCOMES_DATA1,
    listOf(),
    listOf(),
    INCOMES_DATA1
)

val EXPENSES_DATA_MONTHS = listOf(
    listOf(),
    EXPENSES_DATA,
    listOf(),
    EXPENSES_DATA,
    EXPENSES_DATA1,
)

const val STATS_ANIMATE_DURATION = 1000

const val USER_NAME = "Райан Гослинг"
const val MONTH_PREVIEW = "ноябрь"
const val GROUP_NAME = "Undefined Behavior"
