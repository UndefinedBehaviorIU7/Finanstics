package com.ub.finanstics.presentation.addGroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ub.finanstics.presentation.settings.profileSettings.ProfileSettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<AddGroupUiState>(AddGroupUiState.Idle())
    private val repository = AddGroupRepository(application.applicationContext)

    val uiState: StateFlow<AddGroupUiState> = _uiState

    fun updateUiState(
        newName: String? = null,
        newData: String? = null,
        newAdmins: MutableList<Int>? = null,
        newUsers: MutableList<Int>? = null
    ) {
        when (val current = _uiState.value) {
            is AddGroupUiState.Idle -> {
                _uiState.value = current.copy(
                    groupName = newName ?: current.groupName,
                    groupData = newData ?: current.groupData,
                    admins = newAdmins ?: current.admins,
                    users = newUsers ?: current.users
                )
            }

            else -> Unit
        }
    }

    fun retry() {

    }
}

