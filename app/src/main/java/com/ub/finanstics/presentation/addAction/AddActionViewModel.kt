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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Error(val str: String) {
    NAME("имя"),
    TYPE("тип действия"),
    MONEY("сумма"),
    DATE("дата"),
    CATEGORY("категория"),
    DESCRIPTION("описание"),
    SERVER("Сервер"),
    OK("ok"),
    UISTATE("uiState")
}
private const val TIME = 3000L


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

//    fun getCategoriesList() {
//        viewModelScope.launch {
//            val categoryNames = repository.getCategoriesNames()
//            _uiState.update { currentState ->
//                when (currentState) {
//                    is AddActionUiState.Idle -> currentState.copy(allCategory = categoryNames)
//                    else -> currentState
//                }
//            }
//        }
//    }

    fun getUserGroupsList() {
        viewModelScope.launch {
            val groups = repository.getUserGroup()
            val groupNames = groups?.map { it } ?: emptyList()
            _uiState.update { currentState ->
                when (currentState) {
                    is AddActionUiState.Idle -> currentState.copy(allGroup = groupNames)
                    else -> currentState
                }
            }
        }
    }

    fun chooseTypeAndLoad(type: ActionType) {
        _uiState.value = AddActionUiState.SelectType(type)

        viewModelScope.launch {
            _uiState.value = AddActionUiState.Loading(
                typeAction = type,
                nameAction = "",
                moneyAction = -1,
                data = "",
                category = "",
                description = "",
                allCategory = listOf(),
                menuExpandedType = false,
                menuExpandedCategory = false,
                allGroup = listOf(),
                groups = listOf(),
                menuExpandedGroup = false
            )

            val categories = repository.getCategoriesNames(type.toInt())

            try {
                val timeout = TIME
                withTimeout(timeout) {
                    val groups = async { repository.getUserGroup() }
                    val groupResult = groups.await()

                    _uiState.value = AddActionUiState.Idle(
                        typeAction = type,
                        nameAction = "",
                        moneyAction = -1,
                        data = "",
                        category = "",
                        description = "",
                        allCategory = categories,
                        menuExpandedType = false,
                        menuExpandedCategory = false,
                        allGroup = groupResult ?: emptyList(),
                        groups = listOf(),
                        menuExpandedGroup = false
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.value = AddActionUiState.Idle(
                    typeAction = type,
                    nameAction = "",
                    moneyAction = -1,
                    data = "",
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
        }
    }

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
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
                )
            }

            else -> {}
        }
    }

    @Suppress("MagicNumber", "LongParameterList", "ComplexMethod", "ReturnCount")
    fun validateIdle(
        state: AddActionUiState.Idle
    ): Error {
        if (state.nameAction.isBlank()) return Error.NAME
        if (state.typeAction == ActionType.NULL) return Error.TYPE
        if (state.moneyAction <= 0) return Error.MONEY
        if (state.data.isBlank()) return Error.DATE
        if (state.category.isBlank()) return Error.CATEGORY

        return Error.OK
    }

    fun createErrorState(
        current: AddActionUiState.Idle,
        error: Error
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
    fun addAction(): Error {
        val current = uiState.value
        var error: Error = Error.UISTATE
        if (current is AddActionUiState.Idle) {
            viewModelScope.launch {
                error = validateIdle(current)
                if (error == Error.OK) {
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

                    val resApi = repository.addActionApi(
                        actionName = current.nameAction,
                        type = current.typeAction.toInt(),
                        value = current.moneyAction,
                        date = dataForApi(current.data),
                        categoryId = categoryDao.getCategoryByName(
                            name = current.category)!!.serverId!!,
                        description = current.description,
                        groups = current.groups
                    )
                    if (resApi != ErrorAddActionApi.Ok) {
                        actionDao.insertAction(action)
                    }
                    if (resApi == ErrorAddActionApi.Ok)
                        _uiState.value = AddActionUiState.Ok
                    else
                        _uiState.value = createErrorState(
                            current = current,
                            error = Error.SERVER
                        )
                } else {
                    _uiState.value = createErrorState(
                        current = current,
                        error = error
                    )
                }
            }
        }
        return error
    }
}
