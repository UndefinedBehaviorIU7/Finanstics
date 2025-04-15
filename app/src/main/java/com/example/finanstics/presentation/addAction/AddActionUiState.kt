package com.example.finanstics.presentation.addAction

sealed class AddActionUiState {
    data class Idle(
        var typeAction: String,
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
        var typeAction: String,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        val errorMessage: String,
        var errorField: List<String>,
        var allCategory: List<String>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean
    ) : AddActionUiState()

    data class Loading(
        var typeAction: String,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean
    ) : AddActionUiState()
}
