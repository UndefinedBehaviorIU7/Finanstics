package com.ub.finanstics.presentation.groupScreens.statsGroup

import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass

sealed class GroupStatsUiState {
    object Loading : GroupStatsUiState()

    data class Calendar(
        val calendar: CalendarClass,
        val all: Boolean,
        val totalBalance: Int
    ) : GroupStatsUiState()

    data class LoadingData(
        val calendar: CalendarClass,
        val all: Boolean,
        val totalBalance: Int
    ) : GroupStatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val calendar: CalendarClass,
        val all: Boolean,
        val totalBalance: Int
    ) : GroupStatsUiState()

    data class Error(
        val message: String
    ) : GroupStatsUiState()
}
