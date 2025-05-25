package com.ub.finanstics.presentation.statsGroup

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.presentation.calendar.CalendarClass
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import com.ub.finanstics.presentation.stats.TIME_UPDATE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class GroupStatsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<GroupStatsUiState>(GroupStatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val repository = GroupStatsRepository(application)
    private var calendar = CalendarClass()
    private var all = false
    private var totalBalance: Int = 0

    private val _isAuth = MutableStateFlow(false)
    var syncJob: Job? = null

    private val _incomes = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val incomes = _incomes.asStateFlow()

    private val _expenses = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val expenses = _expenses.asStateFlow()

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
                        all = false,
                        totalBalance = 0
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
                                all = false,
                                totalBalance = 0
                            )
                        } else {
                            _incomes.value = incomes
                            _expenses.value = expenses

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

    fun getData(): GroupStatsUiState {
        var res: GroupStatsUiState? = null
        res = GroupStatsUiState.LoadingData(calendar, all, 0)
        viewModelScope.launch {
            try {
                val totalIncomes = repository.getAllIncomes()
                val totalExpenses = repository.getAllExpenses()
                val totalBalance = repository.balance(
                    totalIncomes,
                    totalExpenses
                )

                if (totalBalance == null) {
                    res = GroupStatsUiState.LoadingData(
                        calendar = calendar,
                        all = false,
                        totalBalance = 0
                    )
                } else {
                    if (all) {
                        res = GroupStatsUiState.Done(
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
                            res = GroupStatsUiState.LoadingData(
                                calendar = calendar,
                                all = false,
                                totalBalance = 0
                            )
                        } else {
                            _incomes.value = incomes
                            _expenses.value = expenses

                            res = GroupStatsUiState.Done(
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
                res = GroupStatsUiState.Error(" ${e.localizedMessage}")
            }
        }
        return res!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autoUpdate() {
        syncJob = viewModelScope.launch {
            while (true) {
                if (!_isAuth.value)
                    loginUpdate()
                if (_isAuth.value)
                    updateData()
                delay(TIME_UPDATE)
            }
        }
    }

    fun cancelUpdate() {
        syncJob?.cancel()
        syncJob = null
    }

    private fun loginUpdate() {
        val prefManager = PreferencesManager(application)
        val encryptedPrefManager = EncryptedPreferencesManager(application)
        val token = encryptedPrefManager.getString("token", "")

        if (token.isNotEmpty()) {
            _isAuth.value = true
        }
    }

    fun updateData() {
        val new = getData()
        if (new is GroupStatsUiState.Done) {
            _uiState.value = GroupStatsUiState.Loading
            _uiState.value = new
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
