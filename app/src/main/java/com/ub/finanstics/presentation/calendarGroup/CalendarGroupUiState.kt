package com.ub.finanstics.presentation.calendar

sealed class CalendarGroupUiState {
    data object Idle : CalendarGroupUiState()
    data object Loading : CalendarGroupUiState()

    data class Error(
        val message: String
    ) : CalendarGroupUiState()

    data class Default(
        val calendar: CalendarClass,
    ) : CalendarGroupUiState()

    data class DrawActions(
        val calendar: CalendarClass,
        val day: DayClass?
    ) : CalendarGroupUiState()
}

