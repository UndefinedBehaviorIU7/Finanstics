package com.ub.finanstics.presentation.groupScreens.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GroupsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchGroups() {
        viewModelScope.launch {
            val result = repository.getGroups()
            _uiState.value = result
        }
    }

    fun searchGroups(query: String) {
        val currentState = _uiState.value

        if (query.isEmpty()) {
            if (currentState is GroupsUiState.Search) {
                _uiState.value = GroupsUiState.All(currentState.groups)
            }
        } else {
            val groups = when (currentState) {
                is GroupsUiState.All -> {
                    currentState.groups
                }

                is GroupsUiState.Search -> {
                    currentState.groups
                }

                else -> {
                    emptyList()
                }
            }

            val filteredGroups = groups.filter {
                it.group.name.contains(query, ignoreCase = true)
            }

            _uiState.value = GroupsUiState.Search(groups, filteredGroups)
        }
    }
}
