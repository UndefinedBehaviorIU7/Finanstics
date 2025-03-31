package com.example.finanstics.presentation.calendar

sealed class CalendarUiState {
    data object Idle : CalendarUiState()
    data object Loading : CalendarUiState()

    data class Error(
        val message: String
    ) : CalendarUiState()

    data class Default(
        val calendar: CalendarClass
    ) : CalendarUiState()

    data class DrawActions(
        val calendar: CalendarClass,
        val actions: List<Action?>
    ) : CalendarUiState()
}
