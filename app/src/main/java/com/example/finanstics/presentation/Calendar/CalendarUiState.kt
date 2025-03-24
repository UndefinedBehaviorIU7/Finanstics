package com.example.calendar

import com.example.calendar.DayClass
import com.example.calendar.MonthNameClass

sealed class CalendarUiState {
    object Idle : CalendarUiState()
    object Loading : CalendarUiState()

    data class Error(
        val message: String
    ) : CalendarUiState()

    data class Success(
        val calendar: CalendarClass
    ) : CalendarUiState()
}
