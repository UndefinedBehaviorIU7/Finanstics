package com.ub.finanstics.presentation.calendar

import com.ub.finanstics.db.Action


sealed class CalendarUiState {
    data object Idle : CalendarUiState()
    data object Loading : CalendarUiState()

    data class Error(
        val message: String
    ) : CalendarUiState()

    data class Default(
        val calendar: CalendarClass,
    ) : CalendarUiState()

    data class DrawActions(
        val calendar: CalendarClass,
        val day: DayClass?
    ) : CalendarUiState()

    data class DrawActionDetail(
        val calendar: CalendarClass,
        val day: DayClass?,
        val action: Action,
        val category: String,
        val type: Int
    ) : CalendarUiState()
}

