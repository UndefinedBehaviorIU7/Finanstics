package com.ub.finanstics.presentation.calendar

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.stats.TIME_UPDATE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState = _uiState.asStateFlow()

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
                calendar.initActionsDay(application)
                val newCalendar = CalendarClass()
                newCalendar.copy(calendar)
                if (uiState is CalendarUiState.Default) {
                    _uiState.value = CalendarUiState.Default(newCalendar)
                } else {
                    if (uiState is CalendarUiState.DrawActions)
                        _uiState.value = CalendarUiState.DrawActions(newCalendar, uiState.day)
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
        val uiState = _uiState.value
        if (uiState is CalendarUiState.DrawActions) {
            _uiState.value = CalendarUiState.DrawActionDetail(
                calendar = calendar,
                day = uiState.day,
                action = action.getActionBD(),
                category = action.getActionCategory(),
                type = action.getActionType()
            )
        }
    }

    fun hideAction() {
        val uiState = _uiState.value
        if (uiState is CalendarUiState.DrawActionDetail) {
            _uiState.value = CalendarUiState.DrawActions(
                calendar = calendar,
                day = uiState.day
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadCalendar() {
        try {
            viewModelScope.launch {
                calendar.initActionsDay(application)
                val day = calendar.getNowDataClass()
                if (day != null && day.getData().getMonth() == calendar.getData().getMonth()) {

                    _uiState.value = CalendarUiState.DrawActions(calendar, day)
                }
                else
                    _uiState.value = CalendarUiState.Default(calendar)
            }
        } catch (e: NullPointerException) {
            _uiState.value = CalendarUiState.Error("Ошибка: данные календаря отсутствуют")
        } catch (e: IllegalStateException) {
            _uiState.value = CalendarUiState.Error("Ошибка: некорректное состояние календаря")
        } catch (e: Exception) {
            _uiState.value = CalendarUiState.Error("Неизвестная ошибка: ${e.message}")
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
        if (_uiState.value is CalendarUiState.Default ||
            _uiState.value is CalendarUiState.DrawActions
        ) {
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarUiState.DrawActions(newCalendar, day)
        }
    }

    fun toDefault() {
        if (_uiState.value is CalendarUiState.DrawActions
        ) {
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarUiState.Default(newCalendar)
        }
    }
}
