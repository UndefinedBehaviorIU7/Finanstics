package com.example.finanstics.presentation.stats

sealed class StatsUiState {
    object Loading : StatsUiState()

    data class Done(
        val incomes: List<Pair<String, Int>>,
        val expenses: List<Pair<String, Int>>,
        val month: Int
    ) : StatsUiState()

    data class Error(
        val message: String
    ) : StatsUiState()
}
