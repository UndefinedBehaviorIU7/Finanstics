package com.example.finanstics.presentation.stats

import com.example.finanstics.db.Action

sealed class DetailsUiState {
    object Default : DetailsUiState()

    data class Detailed(
        val actions: List<Action>,
        val chosen: String,
        val type: Int
    ) : DetailsUiState()

    data class DetailedAction(
        val actions: List<Action>,
        val chosen: String,
        val action: Action,
        val type: Int
    ) : DetailsUiState()
}
