package com.example.finanstics.presentation.stats

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.finanstics.db.Category
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.db.syncData
import com.example.finanstics.presentation.calendar.CalendarClass
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import com.example.finanstics.ui.theme.MIN_CATEGORIES_SIZE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TIME_UPDATE = 1000L

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("TooGenericExceptionCaught")
class StatsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()
    var tagStr: String = ""

    private val _isAuth = MutableStateFlow(false)
    val isAuth: StateFlow<Boolean> = _isAuth.asStateFlow()
    var syncJob: Job? = null

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = StatsRepository(db)

    private var calendar = CalendarClass()
    private var totalBalance: Int = 0
    var actions = mutableStateListOf<Triple<String, Int, Int>>()
    var incomes = mutableStateListOf<Pair<String, Int>>()
    var expenses = mutableStateListOf<Pair<String, Int>>()

    init {
        initialisation()
        loginUpdate()
        loadCalendar()
    }

    fun initialisation() {
        viewModelScope.launch {
            val cats = db.categoryDao().getAllCategories()
            println(cats)
            println(cats.size)
            if (cats.size < MIN_CATEGORIES_SIZE) {
                db.categoryDao().insertCategory(Category(name = "Еда", type = 0, serverId = null))
                db.categoryDao().insertCategory(Category(name = "Транспорт", type = 0))
                db.categoryDao().insertCategory(Category(name = "Налоги/штрафы", type = 0))
                db.categoryDao().insertCategory(Category(name = "Покупки", type = 0))
                db.categoryDao().insertCategory(Category(name = "Спорт", type = 0))
                db.categoryDao().insertCategory(Category(name = "Развлечения", type = 0))
                db.categoryDao().insertCategory(Category(name = "Образование", type = 0))
                db.categoryDao().insertCategory(Category(name = "Уход за собой", type = 0))
                db.categoryDao().insertCategory(Category(name = "Здоровье", type = 0))
                db.categoryDao().insertCategory(Category(name = "Быт", type = 0))
                db.categoryDao().insertCategory(Category(name = "Прочие расходы", type = 0))

                db.categoryDao().insertCategory(Category(name = "Зарплата", type = 2))
                db.categoryDao().insertCategory(Category(name = "Перевод", type = 1))
                db.categoryDao().insertCategory(Category(name = "Стипендия", type = 2))
                db.categoryDao().insertCategory(Category(name = "Пенсия", type = 2))
                db.categoryDao().insertCategory(Category(name = "Проценты", type = 2))
                db.categoryDao().insertCategory(Category(name = "Прочие доходы", type = 2))
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCalendar() {
        try {
            _uiState.value = StatsUiState.Loading
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
                if (newTotalBalance != totalBalance) {
                    totalBalance = newTotalBalance
                    update = true
                }
                val incomesData = repository.getIncomes(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )
                val incTmp = mutableStateListOf<Pair<String, Int>>()
                incTmp.addAll(incomesData)

                if (incTmp.size != incomes.size) {
                    update = true
                    incomes.clear()
                    incomes.addAll(incomesData)
                }

                val expensesData = repository.getExpenses(
                    calendar.getData().getMonth(),
                    calendar.getData().getYear()
                )

                val expTmp = mutableStateListOf<Pair<String, Int>>()
                expTmp.addAll(expensesData)

                if (expTmp.size != expenses.size) {
                    update = true
                    expenses.clear()
                    expenses.addAll(expensesData)
                }

                if (update) {
                    _uiState.value = StatsUiState.Done(
                        incomes = incomes,
                        expenses = expenses,
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
        val id = prefManager.getInt("id", 0)
        val tag = prefManager.getString("tag", "")
        val token = prefManager.getString("token", "")

        println("id = $id, tag = $tag, token = $token")
        if (token.isNotEmpty()) {
            _isAuth.value = true
            tagStr = tag
        }
    }

    fun logOut() {
        val prefManager = PreferencesManager(application)
        prefManager.saveData("id", 0)
        prefManager.saveData("tag", "")
        prefManager.saveData("token", "")
        _isAuth.value = false
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
        println("month ${newCalendar.getData().getMonth()}")
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
        _uiState.value = StatsUiState.Calendar(newCalendar, totalBalance)
    }

    fun balance(
        incomes: List<Pair<String, Int>>,
        expenses: List<Pair<String, Int>>
    ): Int {
        return repository.balance(incomes, expenses)
    }
}
