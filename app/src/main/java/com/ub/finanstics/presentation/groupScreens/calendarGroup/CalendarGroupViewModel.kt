package com.ub.finanstics.presentation.groupScreens.calendarGroup

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.userScreens.calendar.ActionDataClass
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.userScreens.calendar.DayClass
import com.ub.finanstics.presentation.userScreens.calendar.ErrorCalendar
import com.ub.finanstics.presentation.userScreens.calendar.MonthNameClass
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.ub.finanstics.presentation.userScreens.stats.TIME_UPDATE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("TooManyFunctions")
class CalendarGroupViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarGroupUiState>(CalendarGroupUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = CalendarGroupRepository()

    val preferencesManager = PreferencesManager(application.applicationContext)
    val groupId = preferencesManager.getInt("groupId", -1)

    private var calendar = CalendarClass()

    private var syncJob: Job? = null

    fun cancelUpdate() {
        syncJob?.cancel()
        syncJob = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autoUpdate() {
        syncJob = viewModelScope.launch {
            while (true) {
                val uiState = _uiState.value
                val newCalendar = CalendarClass()
                calendar.initActionsDayByApi(groupId)
                newCalendar.copy(calendar)
                if (uiState is CalendarGroupUiState.Default) {
                    _uiState.value = CalendarGroupUiState.Default(newCalendar)
                } else {
                    if (uiState is CalendarGroupUiState.DrawActions)
                        _uiState.value = CalendarGroupUiState.DrawActions(newCalendar, uiState.day)
                }
                delay(TIME_UPDATE)
            }
        }
    }

    init {
        viewModelScope.launch {
            loadCalendar()
        }
    }

    fun getCalendarMonth(): MonthNameClass {
        return calendar.getData().getMonth()
    }

    fun viewAction(action: ActionDataClass) {
        viewModelScope.launch {
            val userName = repository.getUserName(action.getUserId()) ?: ""

            val image = repository.userImage(action.getUserId())

            val uiState = _uiState.value
            if (uiState is CalendarGroupUiState.DrawActions) {
                _uiState.value = CalendarGroupUiState.DrawActionDetail(
                    calendar = calendar,
                    day = uiState.day,
                    action = action.getActionAPI(),
                    category = action.getActionCategory(),
                    type = action.getActionType(),
                    name = userName,
                    imageBitmap = image
                )
            }
        }
    }

    fun hideAction() {
        val uiState = _uiState.value
        if (uiState is CalendarGroupUiState.DrawActionDetail) {
            _uiState.value = CalendarGroupUiState.DrawActions(
                calendar = calendar,
                day = uiState.day
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCalendar() {
        viewModelScope.launch {
            try {
                _uiState.value = CalendarGroupUiState.Loading
                if (calendar.initActionsDayByApi(groupId) != ErrorCalendar.ERRORSERVER) {
                    val day = calendar.getNowDataClass()
                    if (day != null) {
                        if (day.getData().getMonth() == calendar.getData().getMonth()) {
                            _uiState.value = CalendarGroupUiState.DrawActions(calendar, day)
                        } else {
                            _uiState.value = CalendarGroupUiState.Default(calendar)
                        }
                    } else {
                        _uiState.value = CalendarGroupUiState.Default(calendar)
                    }
                } else {
                    _uiState.value = CalendarGroupUiState.Error(ErrorCalendar.ERRORSERVER)
                }
            } catch (_: NullPointerException) {
                _uiState.value = CalendarGroupUiState.Error(ErrorCalendar.ERRORSERVER)
            } catch (_: IllegalStateException) {
                _uiState.value = CalendarGroupUiState.Error(ErrorCalendar.ERRORSERVER)
            } catch (_: Exception) {
                _uiState.value = CalendarGroupUiState.Error(ErrorCalendar.ERRORSERVER)
            }
        }
    }

    fun nextMonth() {
        calendar.nextMonth()
        loadCalendar()
    }

    fun lastMonth() {
        calendar.lastMonth()
        loadCalendar()
    }

    fun actions(
        day: DayClass
    ) {
        if (_uiState.value is CalendarGroupUiState.Default ||
            _uiState.value is CalendarGroupUiState.DrawActions
        ) {
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarGroupUiState.DrawActions(newCalendar, day)
        }
    }

    fun toDefault() {
        if (_uiState.value is CalendarGroupUiState.DrawActions
        ) {
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarGroupUiState.Default(newCalendar)
        }
    }
}
