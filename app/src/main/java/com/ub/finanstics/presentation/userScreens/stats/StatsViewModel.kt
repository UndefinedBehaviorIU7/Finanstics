package com.ub.finanstics.presentation.userScreens.stats

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.db.syncData
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.ub.finanstics.ui.theme.MIN_CATEGORIES_SIZE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

const val TIME_UPDATE = 1000L

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("TooGenericExceptionCaught")
class StatsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    var tagStr: String = ""

    private val _isAuth = MutableStateFlow(false)
    var syncJob: Job? = null

    val db = FinansticsDatabase.getDatabase(application)
    private val repository = StatsRepository(application)

    private var calendar = CalendarClass()
    private var totalBalance: Int = 0

    private val _actions = MutableStateFlow<List<Triple<String, Int, Int>>>(emptyList())
    private val _incomes = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    private val _expenses = MutableStateFlow<List<Pair<String, Int>>>(emptyList())

    val actions = _actions.asStateFlow()
    val incomes = _incomes.asStateFlow()
    val expenses = _expenses.asStateFlow()

    private val _date = MutableStateFlow(CalendarClass())
    val date = _date.asStateFlow()

    init {
        initialisation()
        loginUpdate()
        loadCalendar()
    }

    private fun initialisation() {
        viewModelScope.launch {
            val cats = db.categoryDao().getAllCategories()
            if (cats.size < MIN_CATEGORIES_SIZE) { repository.initCategories() }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCalendar() {
        try {
            _uiState.value = StatsUiState.Loading
            _date.value = calendar
            _uiState.value = StatsUiState.Calendar(calendar, 0)
        } catch (e: NullPointerException) {
            _uiState.value = StatsUiState.Error("Ошибка: данные календаря отсутствуют")
        } catch (e: IllegalStateException) {
            _uiState.value = StatsUiState.Error("Ошибка: некорректное состояние календаря")
        } catch (e: Exception) {
            _uiState.value = StatsUiState.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                var update = false
                val newTotalBalance = repository.balance(
                    repository.getAllIncomes(),
                    repository.getAllExpenses()
                )
                if (newTotalBalance != totalBalance || _uiState.value !is StatsUiState.Done) {
                    totalBalance = newTotalBalance
                    update = true
                }
                val incomesData = repository.getIncomes(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )
                val incTmp = mutableStateListOf<Pair<String, Int>>()
                incTmp.addAll(incomesData)

                if (incTmp.size != _incomes.value.size || _uiState.value !is StatsUiState.Done) {
                    update = true
                    _incomes.value = emptyList()
                    _incomes.value = incomesData
                }

                val expensesData = repository.getExpenses(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )

                val expTmp = mutableStateListOf<Pair<String, Int>>()
                expTmp.addAll(expensesData)

                if (expTmp.size != _expenses.value.size || _uiState.value !is StatsUiState.Done) {
                    update = true
                    _expenses.value = emptyList()
                    _expenses.value = expensesData
                }

                if (update) {
                    _uiState.value = StatsUiState.Done(
                        incomes = _incomes.value,
                        expenses = _expenses.value,
                        calendar = calendar,
                        totalBalance = totalBalance
                    )
                }
            } catch (e: HttpException) {
                _uiState.value = StatsUiState.Error(" ${e.localizedMessage}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun autoUpdate() {
        syncJob = viewModelScope.launch {
            while (true) {
                if (!_isAuth.value)
                    loginUpdate()
                fetchData()
                if (_isAuth.value)
                    syncData(application)
                delay(TIME_UPDATE)
            }
        }
    }

    private fun loginUpdate() {
        val prefManager = PreferencesManager(application)
        val encryptedPrefManager = EncryptedPreferencesManager(application)
        val tag = prefManager.getString("tag", "")
        val token = encryptedPrefManager.getString("token", "")

        if (token.isNotEmpty()) {
            _isAuth.value = true
            tagStr = tag
        }
    }

    fun cancelUpdate() {
        syncJob?.cancel()
        syncJob = null
    }

    fun lastMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is StatsUiState.Calendar -> current.calendar.deepCopy().apply { lastMonth() }
            is StatsUiState.LoadingData -> current.calendar.deepCopy().apply { lastMonth() }
            is StatsUiState.Done -> current.calendar.deepCopy().apply { lastMonth() }
            else -> return
        }
        _date.value = newCalendar
        _uiState.value = StatsUiState.Calendar(newCalendar, totalBalance)
    }

    fun nextMonth() {
        val current = uiState.value
        val newCalendar = when (current) {
            is StatsUiState.Calendar -> current.calendar.deepCopy().apply { nextMonth() }
            is StatsUiState.LoadingData -> current.calendar.deepCopy().apply { nextMonth() }
            is StatsUiState.Done -> current.calendar.deepCopy().apply { nextMonth() }
            else -> return
        }
        _date.value = newCalendar
        _uiState.value = StatsUiState.Calendar(newCalendar, totalBalance)
    }

    fun balance(
        incomes: List<Pair<String, Int>>,
        expenses: List<Pair<String, Int>>
    ): Int {
        return repository.balance(incomes, expenses)
    }
}
