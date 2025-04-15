package com.example.finanstics.presentation.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState = _uiState.asStateFlow()

    //    private val calendardata = java.util.Calendar.getInstance()
    private var calendar = CalendarClass()

    init {
        loadCalendar()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadCalendar() {
        try {
            _uiState.value = CalendarUiState.Loading
            _uiState.value = CalendarUiState.DrawActions(calendar, CalendarClass.getNowDay())
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
            calendar.nextMonth()
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarUiState.Default(newCalendar)
        }
    }

    fun lastMonth() {
        Log.d("CalendarViewModel", "Previous month clicked")
        if (_uiState.value is CalendarUiState.Default ||
            _uiState.value is CalendarUiState.DrawActions
        ) {
            calendar.lastMonth()
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            val day = CalendarClass.getNowDay()
            if (day.getData().getMonth() == calendar.getData().getMonth())
                _uiState.value = CalendarUiState.DrawActions(newCalendar, day)
            else
                _uiState.value = CalendarUiState.Default(newCalendar)
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
