package com.ub.finanstics.presentation.groupScreens.statsGroup

import android.app.Application
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.R
import com.ub.finanstics.presentation.userScreens.calendar.CalendarClass
import com.ub.finanstics.presentation.converters.base64ToBitmap
import com.ub.finanstics.presentation.preferencesManagers.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManagers.PreferencesManager
import com.ub.finanstics.presentation.userScreens.stats.TIME_UPDATE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Suppress("TooManyFunctions")
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

    private val preferencesManager = PreferencesManager(application)
    val groupId = preferencesManager.getInt("groupId", 0)
    val groupName = preferencesManager.getString("groupName", "")
    var groupImage: Bitmap? = base64ToBitmap(
        preferencesManager.getString("groupImage", "")
    )

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
            _uiState.value = GroupStatsUiState.Error(application.getString(R.string.no_calendar))
        } catch (e: IllegalStateException) {
            _uiState.value = GroupStatsUiState.Error(
                application.getString(R.string.invalid_calendar_state)
            )
        } catch (e: Exception) {
            _uiState.value = GroupStatsUiState.Error(
                application.getString(R.string.unknown_error) + ": ${e.message}"
            )
        }
    }

    @Suppress("LongMethod")
    fun fetchData() {
        updateGroupImage()
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
                        _incomes.value = totalIncomes
                        _expenses.value = totalExpenses
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

    @Suppress("LongMethod")
    fun getData(): GroupStatsUiState {
        var res: GroupStatsUiState? = null
        updateGroupImage()
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
        val encryptedPrefManager = EncryptedPreferencesManager(application)
        val token = encryptedPrefManager.getString("token", "")

        if (token.isNotEmpty()) {
            _isAuth.value = true
        }
    }

    private fun updateData() {
        val new = getData()
        if (new is GroupStatsUiState.Done) {
            _uiState.value = GroupStatsUiState.Loading
            _uiState.value = new
        }
    }

    fun lastMonth() {
        val newCalendar = when (val current = uiState.value) {
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
        val newCalendar = when (val current = uiState.value) {
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
        updateGroupImage()
        _uiState.value = GroupStatsUiState.Calendar(calendar, all, totalBalance)
    }

    fun balance(
        incomes: List<Pair<String, Int>>?,
        expenses: List<Pair<String, Int>>?
    ): Int? {
        return repository.balance(incomes, expenses)
    }

    private fun updateGroupImage() {
        viewModelScope.launch {
            val image = repository.getGroupImage(groupId)
            if (image != null) {
                groupImage = image
            }
        }
    }
}
