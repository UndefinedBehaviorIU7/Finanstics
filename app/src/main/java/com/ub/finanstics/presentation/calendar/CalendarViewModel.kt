package com.ub.finanstics.presentation.calendar

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState = _uiState.asStateFlow()

    //    private val calendardata = java.util.Calendar.getInstance()
    private var calendar = CalendarClass()

    init {
        viewModelScope.launch {
            loadCalendar()
        }
        startAutoRefresh()
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

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                try {
                    calendar.initActionsDay(application)
                } catch (e: Exception) {
                    Log.e("CalendarAutoRefresh", "Ошибка при обновлении: ${e.message}")
                }
                kotlinx.coroutines.delay(5000L)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadCalendar() {
        try {
            viewModelScope.launch {
                calendar.initActionsDay(application)
                _uiState.value = CalendarUiState.Loading
                _uiState.value = CalendarUiState.DrawActions(calendar, calendar.getNowDataClass())
                Log.d("CalendarClass", CalendarClass.getNowDay().getActions().size.toString())
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
        Log.d("CalendarViewModel", "Next month clicked")
        if (_uiState.value is CalendarUiState.Default ||
            _uiState.value is CalendarUiState.DrawActions
        ) {
            viewModelScope.launch {
                calendar.nextMonth()
                val newCalendar = CalendarClass()
                newCalendar.copy(calendar)
                newCalendar.initActionsDay(application)
                _uiState.value = CalendarUiState.Default(newCalendar)
                var day = CalendarClass.getNowDay()
                if (day.getData().getMonth() == calendar.getData().getMonth()) {
                    day = calendar.getNowDataClass()
                    _uiState.value = CalendarUiState.DrawActions(newCalendar, day)
                }
                else
                    _uiState.value = CalendarUiState.Default(newCalendar)
            }
        }
    }

    fun lastMonth() {
        Log.d("CalendarViewModel", "Previous month clicked")
        if (_uiState.value is CalendarUiState.Default ||
            _uiState.value is CalendarUiState.DrawActions
        ) {
            viewModelScope.launch {
                calendar.lastMonth()
                val newCalendar = CalendarClass()
                newCalendar.copy(calendar)
                newCalendar.initActionsDay(application)
                var day = CalendarClass.getNowDay()
                if (day.getData().getMonth() == calendar.getData().getMonth()) {
                    day = calendar.getNowDataClass()
                    _uiState.value = CalendarUiState.DrawActions(newCalendar, day)
                }
                else
                    _uiState.value = CalendarUiState.Default(newCalendar)
            }
        }
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
