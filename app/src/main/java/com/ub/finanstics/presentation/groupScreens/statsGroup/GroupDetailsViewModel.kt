package com.ub.finanstics.presentation.groupScreens.statsGroup

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.api.ApiRepository
import com.ub.finanstics.api.models.Action
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.ub.finanstics.presentation.userScreens.stats.TIME_UPDATE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("TooGenericExceptionCaught", "TooManyFunctions")
class GroupDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = GroupDetailsRepository(application)

    private val _uiState = MutableStateFlow<GroupDetailsUiState>(GroupDetailsUiState.Default)
    val uiState = _uiState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _all = MutableStateFlow(false)
    val all = _all.asStateFlow()

    var date = CalendarClass()
    private val _chosenCategory = MutableStateFlow(Pair("", -1))
    val chosenCategory: StateFlow<Pair<String, Int>> = _chosenCategory.asStateFlow()

    val apiRep = ApiRepository()
    val prefManager = PreferencesManager(application)
    val groupId = prefManager.getInt("groupId", -1)

    private val _image = MutableStateFlow<Bitmap?>(null)
    val image = _image.asStateFlow()

    var syncJob: Job? = null

    fun changeAllTime() {
        val current = _all.value
        _all.value = !current
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autoUpdate() {
        syncJob = viewModelScope.launch {
            while (true) {
                val current = _uiState.value
                _uiState.value = GroupDetailsUiState.Default
                _uiState.value = current
                delay(TIME_UPDATE)
            }
        }
    }

    fun cancelUpdate() {
        syncJob?.cancel()
        syncJob = null
    }

    fun getDetailedActions(
        category: String,
        type: Int
    ) {
        viewModelScope.launch {
            try {
                val actResp = if (_all.value) apiRep.getGroupActionsByCategory(
                    groupId,
                    category,
                    type
                ) else apiRep.getGroupActionsByCategoryAndDate(
                    groupId, category,
                    type,
                    date.getData().getYear(),
                    date.getData().getMonth().number
                )
                if (actResp.isSuccessful) {
                    val actionsR = actResp.body()
                    if (actionsR != null) {
                        val actions = actionsR.sortedByDescending { it.value }
                        _uiState.value = GroupDetailsUiState.Detailed(
                            chosen = category,
                            type = type,
                            actions = actions
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun hideDetailedActions() {
        _chosenCategory.value = Pair("", -1)
        _uiState.value = GroupDetailsUiState.Default
    }

    fun viewAction(action: Action) {
        viewModelScope.launch {
            _image.value = repository.getUserImage(action.userId)
            val uiState = _uiState.value
            if (uiState is GroupDetailsUiState.Detailed) {
                _uiState.value = GroupDetailsUiState.DetailedAction(
                    actions = uiState.actions,
                    chosen = uiState.chosen,
                    action = action,
                    ownerName = _name.value,
                    type = uiState.type,
                    imageBitmap = _image.value
                )
            }
        }
    }

    fun getActionOwner(userId: Int) {
        viewModelScope.launch {
            try {
                val ownerName = repository.getUserName(userId)
                if (ownerName != null) _name.value = ownerName
            } catch (_: Exception) {
            }
        }
    }

    fun forgetActionOwner() {
        _name.value = ""
    }

    fun hideAction() {
        val uiState = _uiState.value
        if (uiState is GroupDetailsUiState.DetailedAction) {
            _uiState.value = GroupDetailsUiState.Detailed(
                actions = uiState.actions,
                chosen = uiState.chosen,
                type = uiState.type
            )
        }
    }

    fun changeState(
        categoryName: String,
        type: Int
    ) {
        if (_uiState.value is GroupDetailsUiState.Detailed) {
            val detailedUiState = _uiState.value as GroupDetailsUiState.Detailed
            if (categoryName == detailedUiState.chosen && type == detailedUiState.type) {
                _chosenCategory.value = Pair("", -1)
                hideDetailedActions()
            } else {
                _chosenCategory.value = Pair(categoryName, type)
                getDetailedActions(categoryName, type)
            }
        } else {
            _chosenCategory.value = Pair(categoryName, type)
            getDetailedActions(categoryName, type)
        }
    }
}
