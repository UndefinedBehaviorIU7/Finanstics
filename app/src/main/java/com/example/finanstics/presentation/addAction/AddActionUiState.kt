package com.example.finanstics.presentation.addAction

sealed class AddActionUiState {
    data class Idle(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean
    ) : AddActionUiState()

    data class Error(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        val error: com.example.finanstics.presentation.addAction.Error,
        var allCategory: List<String>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean
    ) : AddActionUiState()

    data class Loading(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean
    ) : AddActionUiState()

    data object Ok : AddActionUiState()
}
