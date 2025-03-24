package com.example.calendar

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.calendar.DayClass
import com.example.calendar.MonthNameClass
import com.example.calendar.CalendarUiState
import kotlinx.coroutines.flow.asStateFlow

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val calendardata = java.util.Calendar.getInstance()
    private var calendar = CalendarClass()

    init {
        loadCalendar()
    }

    // Метод для загрузки календаря
    private fun loadCalendar() {
        try {
            _uiState.value = CalendarUiState.Loading
            _uiState.value = CalendarUiState.Success(calendar)
        } catch (e: Exception) {
            _uiState.value = CalendarUiState.Error("Ошибка при загрузке календаря: ${e.message}")
        }
    }

    fun nextMonth() {
        Log.d("CalendarViewModel", "Next month clicked")
        if (_uiState.value is CalendarUiState.Success) {
            // Меняем месяц на следующий
            calendar.NextMount() // изменяем данные в календаре
            // Создаем новый экземпляр календаря с обновленными данными
            val newCalendar = CalendarClass()
            newCalendar.day = calendar.day
            newCalendar.month = calendar.month
            newCalendar.year = calendar.year
            newCalendar.Days = calendar.Days
            // Обновляем UI с новым экземпляром календаря
            _uiState.value = CalendarUiState.Success(newCalendar)
        }
    }

    // Обработчик для предыдущего месяца
    fun previousMonth() {
        Log.d("CalendarViewModel", "Previous month clicked")
        if (_uiState.value is CalendarUiState.Success) {
            // Меняем месяц на предыдущий
            calendar.LastMount() // изменяем данные в календаре
            // Создаем новый экземпляр календаря с обновленными данными
            val newCalendar = CalendarClass()
            newCalendar.day = calendar.day
            newCalendar.month = calendar.month
            newCalendar.year = calendar.year
            newCalendar.Days = calendar.Days
            // Обновляем UI с новым экземпляром календаря
            _uiState.value = CalendarUiState.Success(newCalendar)
        }
    }
}