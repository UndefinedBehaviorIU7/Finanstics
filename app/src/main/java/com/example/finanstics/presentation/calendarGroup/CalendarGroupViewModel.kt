package com.example.finanstics.presentation.calendar

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
class CalendarGroupViewMode(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarGroupUiState>(CalendarGroupUiState.Loading)
    val uiState = _uiState.asStateFlow()

    //    private val calendardata = java.util.Calendar.getInstance()
    private var calendar = CalendarClass()

    init {
        viewModelScope.launch {
            calendar.initActionsDayByApi(application, 2)
            loadCalendar()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadCalendar() {
        try {
            viewModelScope.launch {
                calendar.initActionsDayByApi(application, 1)
                _uiState.value = CalendarGroupUiState.Loading
                _uiState.value = CalendarGroupUiState.DrawActions(calendar, CalendarClass.getNowDay())
            }
        } catch (e: NullPointerException) {
            _uiState.value = CalendarGroupUiState.Error("Ошибка: данные календаря отсутствуют")
        } catch (e: IllegalStateException) {
            _uiState.value = CalendarGroupUiState.Error("Ошибка: некорректное состояние календаря")
        } catch (e: Exception) {
            _uiState.value = CalendarGroupUiState.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    fun nextMonth() {
        Log.d("CalendarViewModel", "Next month clicked")
        if (_uiState.value is CalendarGroupUiState.Default ||
            _uiState.value is CalendarGroupUiState.DrawActions
        ) {
            viewModelScope.launch {
                calendar.nextMonth()
                val newCalendar = CalendarClass()
                newCalendar.copy(calendar)
                calendar.initActionsDayByApi(application, 1)
                _uiState.value = CalendarGroupUiState.Default(newCalendar)
                val day = CalendarClass.getNowDay()
                if (day.getData().getMonth() == calendar.getData().getMonth())
                    _uiState.value = CalendarGroupUiState.DrawActions(newCalendar, day)
                else
                    _uiState.value = CalendarGroupUiState.Default(newCalendar)
            }
        }
    }

    fun lastMonth() {
        Log.d("CalendarViewModel", "Previous month clicked")
        if (_uiState.value is CalendarGroupUiState.Default ||
            _uiState.value is CalendarGroupUiState.DrawActions
        ) {
            viewModelScope.launch {
                calendar.lastMonth()
                val newCalendar = CalendarClass()
                newCalendar.copy(calendar)
                calendar.initActionsDayByApi(application, 1)
                val day = CalendarClass.getNowDay()
                if (day.getData().getMonth() == calendar.getData().getMonth())
                    _uiState.value = CalendarGroupUiState.DrawActions(newCalendar, day)
                else
                    _uiState.value = CalendarGroupUiState.Default(newCalendar)

            }
        }
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
