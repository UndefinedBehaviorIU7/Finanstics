package com.ub.finanstics.presentation.addGroup

import android.app.Application
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionErrors
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.settings.profileSettings.ProfileSettingsUiState
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
        newTagErr: Boolean? = null
    ) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                _uiState.value = current.copy(
                    groupName = newName ?: current.groupName,
                    groupData = newData ?: current.groupData,
                    userInput = newUserInput ?: current.userInput,
                    tagInputErr = newTagErr ?: current.tagInputErr
                )
            }

            else -> Unit
        }
    }

    fun createGroup() {
        _uiState.value = AddGroupUiState.Loading

    }

    fun deleteTag(tag: String) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                for (i in current.users.indices) {
                    if (current.users[i].tag == tag) {
                        current.users.removeAt(i)
                    }
                }
            }

            else -> Unit
        }
    }

    fun addTag(tag: String) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                viewModelScope.launch {
                    val res = repository.getUserByTag(tag)
                    if (res == -1) {
                        _uiState.value = current.copy(
                            tagInputErr = true,
                            errorMsg = "Пользователь не найден"
                        )
                    } else {
                        var unique = true
                        for (item in current.users) {
                            if (item.tag == tag) {
                                unique = false
                                _uiState.value = current.copy(
                                    tagInputErr = true,
                                    errorMsg = "Пользователь уже добавлен"
                                )
                                break
                            }
                        }

                        if (unique) {
                            current.users.add(User(res, tag))
                            _uiState.value = current.copy()
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}

