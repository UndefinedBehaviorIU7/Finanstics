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

    private fun loadCalendar() {
        try {
            _uiState.value = CalendarUiState.Loading
            _uiState.value = CalendarUiState.Default(calendar)
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
            calendar.nextMount()
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
            calendar.lastMount()
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarUiState.Default(newCalendar)
        }
    }

    fun actions(
        action: Array<Action?>
    ) {
        if (_uiState.value is CalendarUiState.Default ||
            _uiState.value is CalendarUiState.DrawActions
        ) {
            val newCalendar = CalendarClass()
            newCalendar.copy(calendar)
            _uiState.value = CalendarUiState.DrawActions(newCalendar, action.toList())
        }
    }
}
