package com.ub.finanstics.presentation.groupScreens.groups

import com.ub.finanstics.api.models.Group
import com.ub.finanstics.api.models.GroupWithImage

sealed class GroupsUiState {
    data class All(
        val groups: List<GroupWithImage>
    ) : GroupsUiState()

    data class Search(
        val groups: List<GroupWithImage>,
        val searchedGroups: List<GroupWithImage>
    ) : GroupsUiState()

    data class Error(
        val groups: List<GroupWithImage>,
        val errorMsg: String
    ) : GroupsUiState()

    data object Loading : GroupsUiState()
}
