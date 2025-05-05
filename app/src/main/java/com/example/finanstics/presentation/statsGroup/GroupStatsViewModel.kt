package com.example.finanstics.presentation.statsGroup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.presentation.calendar.CalendarClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GroupStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<GroupStatsUiState>(GroupStatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val repository = GroupStatsRepository()
    private var calendar = CalendarClass()
    private var all = false
    private var totalBalance: Int = 0

    init {
        loadCalendar()
        fetchData()
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCalendar() {
        try {
            _uiState.value = GroupStatsUiState.Loading
            _uiState.value = GroupStatsUiState.Calendar(calendar, all, 0)
        } catch (e: NullPointerException) {
            _uiState.value = GroupStatsUiState.Error("Ошибка: данные календаря отсутствуют")
        } catch (e: IllegalStateException) {
            _uiState.value = GroupStatsUiState.Error("Ошибка: некорректное состояние календаря")
        } catch (e: Exception) {
            _uiState.value = GroupStatsUiState.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    fun fetchData() {
        _uiState.value = GroupStatsUiState.LoadingData(calendar, all, 0)
        viewModelScope.launch {
            try {
                val totalIncomes = repository.getAllIncomes()
                val totalExpenses = repository.getAllExpenses()
                val totalBalance = repository.balance(
                    totalIncomes,
                    totalExpenses
                )
                if (totalBalance == null) {
                    _uiState.value = GroupStatsUiState.LoadingData(
                        calendar = calendar,
                        all = false, 0
                    )
                } else {
                    if (all) {
                        _uiState.value = GroupStatsUiState.Done(
                            incomes = totalIncomes!!,
                            expenses = totalExpenses!!,
                            calendar = calendar,
                            all = all,
                            totalBalance = totalBalance
                        )
                    } else {
                        val incomes = repository.getIncomes(
                            calendar.getData().getMonth(),
                            calendar.getData().getYear()
                        )
                        val expenses = repository.getExpenses(
                            calendar.getData().getMonth(),
                            calendar.getData().getYear()
                        )
                        if (incomes == null || expenses == null) {
                            _uiState.value = GroupStatsUiState.LoadingData(
                                calendar = calendar,
                                all = false, 0
                            )
                        } else {
                            _uiState.value = GroupStatsUiState.Done(
                                incomes = incomes,
                                expenses = expenses,
                                calendar = calendar,
                                all = all,
                                totalBalance = totalBalance
                            )
                        }
                    }

                }
            } catch (e: HttpException) {
                _uiState.value = GroupStatsUiState.Error(" ${e.localizedMessage}")
            }
        }
    }

    fun lastMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is GroupStatsUiState.Calendar -> current.calendar.deepCopy().apply { lastMonth() }
            is GroupStatsUiState.LoadingData -> current.calendar.deepCopy().apply { lastMonth() }
            is GroupStatsUiState.Done -> current.calendar.deepCopy().apply { lastMonth() }
            else -> return
        }
        println("month ${newCalendar.getData().getMonth()}")
        all = false
        calendar = calendar.deepCopy()
        _uiState.value = GroupStatsUiState.Calendar(newCalendar, all, totalBalance)
    }

    fun nextMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is GroupStatsUiState.Calendar -> current.calendar.deepCopy().apply { nextMonth() }
            is GroupStatsUiState.LoadingData -> current.calendar.deepCopy().apply { nextMonth() }
            is GroupStatsUiState.Done -> current.calendar.deepCopy().apply { nextMonth() }
            else -> return
        }
        all = false
        calendar = calendar.deepCopy()
        _uiState.value = GroupStatsUiState.Calendar(newCalendar, all, totalBalance)
    }

    fun switchAll() {
        all = !all
        _uiState.value = GroupStatsUiState.Calendar(calendar, all, totalBalance)
    }

    fun balance(
        incomes: List<Pair<String, Int>>?,
        expenses: List<Pair<String, Int>>?
    ): Int? {
        return repository.balance(incomes, expenses)
    }
}
