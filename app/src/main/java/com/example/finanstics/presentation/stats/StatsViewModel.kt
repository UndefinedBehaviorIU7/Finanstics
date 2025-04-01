package com.example.finanstics.presentation.stats

import android.app.Application
import retrofit2.HttpException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.presentation.calendar.CalendarClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val repository = StatsRepository()

    private var calendar = CalendarClass()

    init {
        loadCalendar()
        fetchData()
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCalendar() {
        try {
            _uiState.value = StatsUiState.Loading
            _uiState.value = StatsUiState.Calendar(calendar)
        } catch (e: NullPointerException) {
            _uiState.value = StatsUiState.Error("Ошибка: данные календаря отсутствуют")
        } catch (e: IllegalStateException) {
            _uiState.value = StatsUiState.Error("Ошибка: некорректное состояние календаря")
        } catch (e: Exception) {
            _uiState.value = StatsUiState.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    fun fetchData() {
        _uiState.value = StatsUiState.LoadingData(calendar)
        viewModelScope.launch {
            try {
                val incomes = repository.getIncomes(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )
                val expenses = repository.getExpenses(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )
                _uiState.value = StatsUiState.Done(
                    incomes = incomes,
                    expenses = expenses,
                    calendar = calendar
                )
            } catch (e: HttpException) {
                _uiState.value = StatsUiState.Error(" ${e.localizedMessage}")
            }
        }
    }

    fun lastMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is StatsUiState.Calendar -> current.calendar.deepCopy().apply { lastMonth() }
            is StatsUiState.LoadingData -> current.calendar.deepCopy().apply { lastMonth() }
            is StatsUiState.Done -> current.calendar.deepCopy().apply { lastMonth() }
            else -> return
        }
        println("month ${newCalendar.getData().getMonth()}")
        _uiState.value = StatsUiState.Calendar(newCalendar)
    }

    fun nextMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is StatsUiState.Calendar -> current.calendar.deepCopy().apply { nextMonth() }
            is StatsUiState.LoadingData -> current.calendar.deepCopy().apply { nextMonth() }
            is StatsUiState.Done -> current.calendar.deepCopy().apply { nextMonth() }
            else -> return
        }
        _uiState.value = StatsUiState.Calendar(newCalendar)
    }
}
