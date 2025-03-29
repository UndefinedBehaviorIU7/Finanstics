package com.example.finanstics.presentation.group.stats

sealed class GroupStatsUiState {
    object Loading : GroupStatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val month: Int
    ) : GroupStatsUiState()

    data class Error(
        val message: String
    ) : GroupStatsUiState()
}
