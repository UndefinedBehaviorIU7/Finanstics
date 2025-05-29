package com.ub.finanstics.presentation.userScreens.stats

import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass

sealed class StatsUiState {
    object Loading : StatsUiState()

    data class Calendar(
        val calendar: CalendarClass,
        val totalBalance: Int
    ) : StatsUiState()

    data class LoadingData(
        val calendar: CalendarClass,
        val totalBalance: Int
    ) : StatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val calendar: CalendarClass,
        val totalBalance: Int
    ) : StatsUiState()

    data class Error(
        val message: String
    ) : StatsUiState()
}
