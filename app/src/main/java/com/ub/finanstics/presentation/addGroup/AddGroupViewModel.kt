package com.ub.finanstics.presentation.addGroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<AddGroupUiState>(AddGroupUiState.Idle())
    private val repository = AddGroupRepository(application.applicationContext)

    val uiState: StateFlow<AddGroupUiState> = _uiState

    fun updateUiState(
        newName: String? = null,
        newData: String? = null,
        newUserInput: String? = null,
        newTagErr: Boolean? = null,
        newNameErr: Boolean? = null,
        newDataErr: Boolean? = null,
        newShow: Boolean? = null
    ) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                _uiState.value = current.copy(
                    groupName = newName ?: current.groupName,
                    groupData = newData ?: current.groupData,
                    userInput = newUserInput ?: current.userInput,
                    tagError = newTagErr ?: current.tagError,
                    nameError = newNameErr ?: current.nameError,
                    dataError = newDataErr ?: current.dataError,
                    showDialog = newShow ?: current.showDialog
                )
            }

            else -> Unit
        }
    }

    fun createGroup() {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                _uiState.value = AddGroupUiState.Loading
                if (current.groupName.isEmpty()) {
                    _uiState.value = current.copy(
                        nameError = true,
                        errorMsg = "Имя группы не может быть пустым"
                    )
                    return
                }

                viewModelScope.launch {
                    val response = repository.createGroup(current)
                }
            }

            else -> Unit
        }
    }

    fun deleteTag(tag: String) {
        val current = _uiState.value
        if (current is AddGroupUiState.Idle) {
            val updated = current.users.filter { it.tag != tag }.toList()
            _uiState.value = current.copy(users = updated)
        }
    }

    fun addTag(tag: String) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                viewModelScope.launch {
                    val userId = repository.getUserByTag(tag)
                    if (userId == -1) {
                        _uiState.value = current.copy(
                            tagError = true,
                            errorMsg = "Пользователь не найден"
                        )
                    } else {
                        var unique = true
                        for (item in current.users) {
                            if (item.tag == tag) {
                                unique = false
                                _uiState.value = current.copy(
                                    tagError = true,
                                    errorMsg = "Пользователь уже добавлен"
                                )
                                break
                            }
                        }

                        if (unique) {
                            val updatedUsers = current.users + User(userId, tag)
                            _uiState.value = current.copy(users = updatedUsers, userInput = "")
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}

