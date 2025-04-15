package com.example.finanstics.presentation.group.stats

import com.example.finanstics.presentation.calendar.CalendarClass

sealed class GroupStatsUiState {
    object Loading : GroupStatsUiState()

    data class Calendar(
        val calendar: CalendarClass,
        val all: Boolean
    ) : GroupStatsUiState()

    data class LoadingData(
        val calendar: CalendarClass,
        val all: Boolean
    ) : GroupStatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val calendar: CalendarClass,
        val all: Boolean
    ) : GroupStatsUiState()

    data class Error(
        val message: String
    ) : GroupStatsUiState()
}
