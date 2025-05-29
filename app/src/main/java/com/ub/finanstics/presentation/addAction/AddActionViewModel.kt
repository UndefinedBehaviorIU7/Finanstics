package com.ub.finanstics.presentation.addAction

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.api.models.Group
import com.ub.finanstics.db.Action
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.DataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.util.Log
import com.ub.finanstics.presentation.addActionGroup.TIME_LOAD
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Calendar

enum class ErrorAddAction(val str: String) {
    NAME("имя"),
    TYPE("тип действия"),
    MONEY("сумма"),
    DATE("дата"),
    CATEGORY("категория"),
    OK("ok"),
    ERROR_LOADING_DATA_SERVER("ошибка загрузки данных с сервера"),
    ERROR_ADD_DATA_SERVER("ошибка загрузки данных на сервер")
}

@RequiresApi(Build.VERSION_CODES.O)
fun dataForApi(dataStr: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dataStr, inputFormatter)
    return date.format(outputFormatter)
}

class AddActionViewModel(
    application: Application
) : AndroidViewModel(application) {

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = AddActionRepository(db, application.applicationContext)
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    private val _uiState = MutableStateFlow<AddActionUiState>(
        AddActionUiState.SelectType(typeAction = ActionType.NULL)
    )

    val uiState = _uiState.asStateFlow()

    private fun getNowData(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return "%02d.%02d.%04d".format(day, month, year)
    }

    @Suppress("TooGenericExceptionCaught", "LongMethod")
    fun tryLoad(type: ActionType) {

        viewModelScope.launch {

            _uiState.value = AddActionUiState.Loading(
                typeAction = type,
            )

            val categories = repository.getCategoriesNames(type.toInt())

            val timeout = TIME_LOAD
            val allGroups = withTimeoutOrNull(timeout) {
                try {
                    repository.getUserGroup()
                } catch (e: Exception) {
                    Log.e("chooseTypeAndLoad", "Error getting categories", e)
                    null
                }
            }.also {
                Log.d("chooseTypeAndLoad", "Categories result: $it")
            }

            if (allGroups == null) {
                _uiState.value = AddActionUiState.ErrorLoad(
                    typeAction = type,
                    allCategory = categories,
                    error = ErrorAddAction.ERROR_LOADING_DATA_SERVER,
                )
            } else {
                _uiState.value = AddActionUiState.Idle(
                    typeAction = type,
                    nameAction = "",
                    moneyAction = -1,
                    data = getNowData(),
                    category = "",
                    description = "",
                    allCategory = categories,
                    menuExpandedType = false,
                    menuExpandedCategory = false,
                    allGroup = allGroups,
                    groups = listOf(),
                    menuExpandedGroup = false
                )
            }
        }
    }

    fun withoutGroups(type: ActionType, categories: List<String>) {
        _uiState.value = AddActionUiState.Idle(
            typeAction = type,
            nameAction = "",
            moneyAction = -1,
            data = getNowData(),
            category = "",
            description = "",
            allCategory = categories,
            menuExpandedType = false,
            menuExpandedCategory = false,
            allGroup = emptyList(),
            groups = listOf(),
            menuExpandedGroup = false
        )
    }

//    fun chooseTypeAndLoad(type: ActionType) {
//        _uiState.value = AddActionUiState.SelectType(type)
//
//        viewModelScope.launch {
//            _uiState.value = AddActionUiState.Loading(
//                typeAction = type,
//                nameAction = "",
//                moneyAction = -1,
//                data = getNowData(),
//                category = "",
//                description = "",
//                allCategory = listOf(),
//                menuExpandedType = false,
//                menuExpandedCategory = false,
//                allGroup = listOf(),
//                groups = listOf(),
//                menuExpandedGroup = false
//            )
//
//            val timeout = 5000L
//            val categories = withTimeoutOrNull(timeout) {
//                try {
//                    repository.getCategoriesByType(type.toInt())
//                } catch (e: Exception) {
//                    Log.e("chooseTypeAndLoad", "Error getting categories", e)
//                    null
//                }
//            }.also {
//                Log.d("chooseTypeAndLoad", "Categories result: $it")
//            }
//
//            val categories = repository.getCategoriesNames(type.toInt())
//
//            try {
//                val timeout = TIME
//                withTimeout(timeout) {
//                    val groups = async { repository.getUserGroup() }
//                    val groupResult = groups.await()
//
//                    _uiState.value = AddActionUiState.Idle(
//                        typeAction = type,
//                        nameAction = "",
//                        moneyAction = -1,
//                        data = getNowData(),
//                        category = "",
//                        description = "",
//                        allCategory = categories,
//                        menuExpandedType = false,
//                        menuExpandedCategory = false,
//                        allGroup = groupResult ?: emptyList(),
//                        groups = listOf(),
//                        menuExpandedGroup = false
//                    )
//                }
//            } catch (e: TimeoutCancellationException) {
//                _uiState.value = AddActionUiState.Idle(
//                    typeAction = type,
//                    nameAction = "",
//                    moneyAction = -1,
//                    data = getNowData(),
//                    category = "",
//                    description = "",
//                    allCategory = categories,
//                    menuExpandedType = false,
//                    menuExpandedCategory = false,
//                    allGroup = emptyList(),
//                    groups = listOf(),
//                    menuExpandedGroup = false
//                )
//            }
//        }
//    }

    @Suppress("MagicNumber", "LongParameterList", "LongMethod", "ComplexMethod")
    fun updateUIState(
        newTypeAction: ActionType? = null,
        newNameAction: String? = null,
        newMoneyAction: Int? = null,
        newData: String? = null,
        newCategory: String? = null,
        newDescription: String? = null,
        newGroups: List<Group>? = null,
        newMenuExpandedType: Boolean? = null,
        newMenuExpandedCategory: Boolean? = null,
        newMenuExpandedGroup: Boolean? = null
    ) {
        when (val current = _uiState.value) {
            is AddActionUiState.Idle -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    groups = newGroups ?: current.groups,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory,
                    menuExpandedGroup = newMenuExpandedGroup ?: current.menuExpandedGroup
                )
            }

            is AddActionUiState.Error -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
                )
            }

            is AddActionUiState.Loading -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                )
            }

            else -> {}
        }
    }

    @Suppress("MagicNumber", "LongParameterList", "ComplexMethod", "ReturnCount")
    fun validateIdle(
        state: AddActionUiState.Idle
    ): ErrorAddAction {
        if (state.nameAction.isBlank()) return ErrorAddAction.NAME
        if (state.typeAction == ActionType.NULL) return ErrorAddAction.TYPE
        if (state.moneyAction <= 0) return ErrorAddAction.MONEY
        if (state.data.isBlank()) return ErrorAddAction.DATE
        if (state.category.isBlank()) return ErrorAddAction.CATEGORY

        return ErrorAddAction.OK
    }

    private fun createErrorState(
        current: AddActionUiState.Idle,
        error: ErrorAddAction
    ): AddActionUiState.Error {
        return AddActionUiState.Error(
            typeAction = current.typeAction,
            nameAction = current.nameAction,
            moneyAction = current.moneyAction,
            data = current.data,
            category = current.category,
            description = current.description,
            error = error,
            allCategory = current.allCategory,
            menuExpandedType = current.menuExpandedType,
            menuExpandedCategory = current.menuExpandedCategory,
            allGroup = current.allGroup,
            groups = current.groups,
            menuExpandedGroup = current.menuExpandedGroup
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAction() {
        val current = when (val state = _uiState.value) {
            is AddActionUiState.Error -> {
                val idle = AddActionUiState.Idle(
                    typeAction = state.typeAction,
                    nameAction = state.nameAction,
                    moneyAction = state.moneyAction,
                    data = state.data,
                    category = state.category,
                    description = state.description,
                    allCategory = state.allCategory,
                    menuExpandedType = state.menuExpandedType,
                    menuExpandedCategory = state.menuExpandedCategory,
                    allGroup = state.allGroup,
                    groups = state.groups,
                    menuExpandedGroup = state.menuExpandedGroup,
                )
                _uiState.value = idle
                idle
            }
            is AddActionUiState.Idle -> state
            else -> return
        }

        val error = validateIdle(current)
        if (error != ErrorAddAction.OK) {
            _uiState.value = createErrorState(current, error)
            return
        }

        viewModelScope.launch {
            _uiState.value = AddActionUiState.Loading(
                typeAction = current.typeAction,
            )

            val result = repository.addActionApi(
                actionName = current.nameAction,
                type = current.typeAction.toInt(),
                value = current.moneyAction,
                date = dataForApi(current.data),
                category = current.category,
                description = current.description,
                groups = current.groups
            )

            if (result == ErrorAddAction.OK) {
                _uiState.value = AddActionUiState.Ok
            } else {
                _uiState.value = createErrorState(current, result)
            }
        }
    }

    fun addActionOnlyLocally() {
        val current = uiState.value
        if (current is AddActionUiState.Error) {
            viewModelScope.launch {
                val data = DataClass.getDataByString(current.data)
                val action = Action(
                    name = current.nameAction,
                    type = current.typeAction.toInt(),
                    description = current.description,
                    value = current.moneyAction,
                    date = LocalDate.of(data.getYear(), data.getMonth().number, data.getDay()),
                    categoryId = categoryDao.getCategoryByName(name = current.category)!!.id,
                    createdAt = "2025-04-22T14:30:00"
                )

                actionDao.insertAction(action)

                _uiState.value = AddActionUiState.Ok
            }
        }
    }

//    @Suppress("TooGenericExceptionCaught")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun addAction1(): ErrorAddAction {
//        val current = uiState.value
//        var errorAddACtion: ErrorAddAction = ErrorAddAction.UISTATE
//        if (current is AddActionUiState.Idle) {
//            viewModelScope.launch {
//                errorAddACtion = validateIdle(current)
//                if (errorAddACtion == ErrorAddAction.OK) {
//                    val data = DataClass.getDataByString(current.data)
//                    val action = Action(
//                        name = current.nameAction,
//                        type = current.typeAction.toInt(),
//                        description = current.description,
//                        value = current.moneyAction,
//                        date = LocalDate.of(data.getYear(), data.getMonth().number, data.getDay()),
//                        categoryId = categoryDao.getCategoryByName(name = current.category)!!.id,
//                        createdAt = "2025-04-22T14:30:00"
//                    )
//
//                    Log.d("AddAction count", "1")
//
//                    try {
//                        val resApi = repository.addActionApi(
//                            actionName = current.nameAction,
//                            type = current.typeAction.toInt(),
//                            value = current.moneyAction,
//                            date = dataForApi(current.data),
//                            categoryId = categoryDao.getCategoryByName(
//                                name = current.category)!!.serverId!!,
//                            description = current.description,
//                            groups = current.groups
//                        )
//                        if (resApi != ErrorAddActionApi.Ok) {
//                            actionDao.insertAction(action)
//                            Log.d("AddAction count", "2")
//                        }
//                        if (resApi == ErrorAddActionApi.Ok)
//                            _uiState.value = AddActionUiState.Ok
//                        else
//                            _uiState.value = createErrorState(
//                                current = current,
//                                errorAddACtion = ErrorAddAction.SERVER
//                            )
//                    } catch (e: Exception) {
//                        actionDao.insertAction(action)
//                        Log.d("AddAction count", "3")
//                    }
//                } else {
//                    _uiState.value = createErrorState(
//                        current = current,
//                        errorAddACtion = errorAddACtion
//                    )
//                }
//            }
//        }
//        return errorAddACtion
//    }
}
