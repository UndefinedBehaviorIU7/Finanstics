package com.ub.finanstics.presentation.addGroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun dismissErrorDialog() {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> _uiState.value = current.copy(showDialog = false)

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
                        errorMsg = "Имя группы не может быть пустым",
                        showDialog = true
                    )
                    return
                }

                viewModelScope.launch {
                    val response = repository.createGroup(current)
                    if (response) {
                        _uiState.value = AddGroupUiState.Success
                    } else {
                        _uiState.value = current.copy(
                            errorMsg = "Ошибка соединения",
                            showDialog = true
                        )
                    }
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
                    when {
                        userId == -1 -> {
                            _uiState.value = current.copy(
                                tagError = true,
                                errorMsg = "Пользователь не найден",
                                showDialog = true
                            )
                            return@launch
                        }
                        userId == -2 -> {
                            _uiState.value = current.copy(
                                tagError = true,
                                errorMsg = "Ошибка соединения",
                                showDialog = true
                            )
                            return@launch
                        }
                        PreferencesManager(application).getInt("id", 0) == userId -> {
                            _uiState.value = current.copy(
                                tagError = true,
                                errorMsg = "Вы — создатель группы",
                                showDialog = true
                            )
                            return@launch
                        }
                        current.users.any { it.tag == tag } -> {
                            _uiState.value = current.copy(
                                tagError = true,
                                errorMsg = "Пользователь уже добавлен",
                                showDialog = true
                            )
                            return@launch
                        }
                        else -> {
                            val updatedUsers = current.users + User(userId, tag)
                            _uiState.value = current.copy(
                                users = updatedUsers,
                                userInput = ""
                            )
                        }
                    }
                }
            }

            else -> Unit
        }
    }

    fun clearSuccessFlag() {
        _uiState.value = AddGroupUiState.Idle()
    }
}

