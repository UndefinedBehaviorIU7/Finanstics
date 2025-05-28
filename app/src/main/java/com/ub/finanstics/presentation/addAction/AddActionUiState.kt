package com.ub.finanstics.presentation.addAction

import com.ub.finanstics.api.models.Group

sealed class AddActionUiState {
    data class Idle(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        var allGroup: List<Group>,
        var groups: List<Group>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val menuExpandedGroup: Boolean,
    ) : AddActionUiState()

    data class Error(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        var allGroup: List<Group>,
        var groups: List<Group>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val menuExpandedGroup: Boolean,
        val error: ErrorAddAction,
    ) : AddActionUiState()

    data class ErrorLoad(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        var allGroup: List<Group>,
        var groups: List<Group>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val menuExpandedGroup: Boolean,
        val error: ErrorAddAction,
    ) : AddActionUiState()

    data class Loading(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<String>,
        var allGroup: List<Group>,
        var groups: List<Group>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val menuExpandedGroup: Boolean,
    ) : AddActionUiState()

    data class SelectType(
        var typeAction: ActionType,
    ) : AddActionUiState()

    data object Ok : AddActionUiState()
}
