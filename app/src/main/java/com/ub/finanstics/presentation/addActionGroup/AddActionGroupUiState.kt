package com.ub.finanstics.presentation.addAction

import com.ub.finanstics.api.models.Category

sealed class AddActionGroupUiState {
    data class Idle(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<Category>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val duplication: Boolean
    ) : AddActionGroupUiState()

    data class Error(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        val error: ErrorAddAction,
        var allCategory: List<Category>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val duplication: Boolean
    ) : AddActionGroupUiState()

    data class Loading(
        var typeAction: ActionType,
        var nameAction: String,
        var moneyAction: Int,
        var data: String,
        var category: String,
        var description: String,
        var allCategory: List<Category>,
        val menuExpandedType: Boolean,
        val menuExpandedCategory: Boolean,
        val duplication: Boolean
    ) : AddActionGroupUiState()

    data class SelectType(
        var typeAction: ActionType,
    ) : AddActionGroupUiState()

    data object Ok : AddActionGroupUiState()
}
