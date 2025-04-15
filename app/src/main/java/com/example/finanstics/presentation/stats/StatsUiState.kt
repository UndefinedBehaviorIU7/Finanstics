package com.example.finanstics.presentation.stats

import com.example.finanstics.presentation.calendar.CalendarClass

sealed class StatsUiState {
    object Loading : StatsUiState()

    data class Calendar(
        val calendar: CalendarClass,
    ) : StatsUiState()

    data class LoadingData(
        val calendar: CalendarClass,
    ) : StatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val calendar: CalendarClass,
    ) : StatsUiState()

    data class Error(
        val message: String
    ) : StatsUiState()
}
