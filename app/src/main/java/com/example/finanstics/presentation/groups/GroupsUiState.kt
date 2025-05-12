package com.example.finanstics.presentation.groups

import com.example.finanstics.api.models.Group

sealed class GroupsUiState {
    data object Idle: GroupsUiState()

    data object Loading: GroupsUiState()

    data class Error(
        val groups: List<Group>,
        val errorMsg: String
    ): GroupsUiState()

    data class All(
        val groups: List<Group>
    ): GroupsUiState()

    data class Search(
        val groups: List<Group>,
        val searchedGroups: List<Group>
    ): GroupsUiState()
}
