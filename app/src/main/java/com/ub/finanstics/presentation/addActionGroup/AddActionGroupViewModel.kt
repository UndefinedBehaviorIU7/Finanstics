package com.ub.finanstics.presentation.addAction

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.db.FinansticsDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Calendar

const val TIME_LOAD = 5000L

class AddActionGroupViewModel(
    application: Application
) : AndroidViewModel(application) {

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = AddActionGroupRepository(db, application.applicationContext)
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    private val _uiState = MutableStateFlow<AddActionGroupUiState>(
        AddActionGroupUiState.SelectType(typeAction = ActionType.NULL)
    )

    val uiState = _uiState.asStateFlow()

    @Suppress("TooGenericExceptionCaught")
    fun getNowData(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return "%02d.%02d.%04d".format(day, month, year)
    }

    @Suppress("TooGenericExceptionCaught")
    fun tryLoad(type: ActionType) {
        viewModelScope.launch {

            _uiState.value = AddActionGroupUiState.Loading(
                typeAction = type,
                nameAction = "",
                moneyAction = -1,
                data = getNowData(),
                category = "",
                description = "",
                allCategory = listOf(),
                menuExpandedType = false,
                menuExpandedCategory = false,
                duplication = false
            )

            val timeout = TIME_LOAD
            val categories = withTimeoutOrNull(timeout) {
                try {
                    repository.getCategoriesByType(type.toInt())
                } catch (e: Exception) {
                    Log.e("chooseTypeAndLoad", "Error getting categories", e)
                    null
                }
            }.also {
                Log.d("chooseTypeAndLoad", "Categories result: $it")
            }

            if (categories == null) {
                _uiState.value = AddActionGroupUiState.Error(
                    typeAction = type,
                    nameAction = "",
                    moneyAction = -1,
                    data = getNowData(),
                    category = "",
                    description = "",
                    allCategory = emptyList(),
                    menuExpandedType = false,
                    menuExpandedCategory = false,
                    duplication = false,
                    error = ErrorAddAction.ERROR_LOADING_DATA_SERVER,
                )
            } else {
                _uiState.value = AddActionGroupUiState.Idle(
                    typeAction = type,
                    nameAction = "",
                    moneyAction = -1,
                    data = getNowData(),
                    category = "",
                    description = "",
                    allCategory = categories,
                    menuExpandedType = false,
                    menuExpandedCategory = false,
                    duplication = false,
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
        newMenuExpandedType: Boolean? = null,
        newMenuExpandedCategory: Boolean? = null,
        newDuplication: Boolean? = null
    ) {
        when (val current = _uiState.value) {
            is AddActionGroupUiState.Idle -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory,
                    duplication = newDuplication ?: current.duplication
                )
            }

            is AddActionGroupUiState.Error -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory,
                    duplication = newDuplication ?: current.duplication
                )
            }

            is AddActionGroupUiState.Loading -> {
                _uiState.value = current.copy(
                    typeAction = newTypeAction ?: current.typeAction,
                    nameAction = newNameAction ?: current.nameAction,
                    moneyAction = newMoneyAction ?: current.moneyAction,
                    data = newData ?: current.data,
                    category = newCategory ?: current.category,
                    description = newDescription ?: current.description,
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory,
                    duplication = newDuplication ?: current.duplication
                )
            }

            else -> {}
        }
    }

    @Suppress("MagicNumber", "LongParameterList", "ComplexMethod", "ReturnCount")
    fun validateIdle(
        state: AddActionGroupUiState.Idle
    ): ErrorAddAction {
        if (state.nameAction.isBlank()) return ErrorAddAction.NAME
        if (state.typeAction == ActionType.NULL) return ErrorAddAction.TYPE
        if (state.moneyAction <= 0) return ErrorAddAction.MONEY
        if (state.data.isBlank()) return ErrorAddAction.DATE
        if (state.category.isBlank()) return ErrorAddAction.CATEGORY

        return ErrorAddAction.OK
    }

    fun createErrorStateGroup(
        current: AddActionGroupUiState.Idle,
        error: ErrorAddAction
    ): AddActionGroupUiState.Error {
        return AddActionGroupUiState.Error(
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
            duplication = current.duplication
        )
    }

    @Suppress("ReturnCount")
    private fun getCategoryIdByName(
        name: String, allCategory: List<Category>
    ): Int? {
        for (el in allCategory) {
            if (el.name == name)
                return el.id
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("ReturnCount", "LongMethod")
    fun addAction() {
        val current = when (val state = _uiState.value) {
            is AddActionGroupUiState.Error -> {
                val idle = AddActionGroupUiState.Idle(
                    typeAction = state.typeAction,
                    nameAction = state.nameAction,
                    moneyAction = state.moneyAction,
                    data = state.data,
                    category = state.category,
                    description = state.description,
                    allCategory = state.allCategory,
                    menuExpandedType = state.menuExpandedType,
                    menuExpandedCategory = state.menuExpandedCategory,
                    duplication = state.duplication
                )
                _uiState.value = idle
                idle
            }
            is AddActionGroupUiState.Idle -> state
            else -> return
        }

        val error = validateIdle(current)
        if (error != ErrorAddAction.OK) {
            _uiState.value = createErrorStateGroup(current, error)
            return
        }

        val categoryId = getCategoryIdByName(current.category, current.allCategory)
        if (categoryId == null) {
            _uiState.value = createErrorStateGroup(current, ErrorAddAction.ERROR_ADD_DATA_SERVER)
            return
        }

        viewModelScope.launch {
            _uiState.value = AddActionGroupUiState.Loading(
                typeAction = current.typeAction,
                nameAction = "",
                moneyAction = -1,
                data = getNowData(),
                category = "",
                description = "",
                allCategory = current.allCategory,
                menuExpandedType = false,
                menuExpandedCategory = false,
                duplication = false,
            )

            val result = repository.addActionApi(
                actionName = current.nameAction,
                type = current.typeAction.toInt(),
                value = current.moneyAction,
                date = dataForApi(current.data),
                categoryId = categoryId,
                description = current.description,
                duplication = current.duplication
            )

            if (result == ErrorAddAction.OK) {
                _uiState.value = AddActionGroupUiState.Ok
            } else {
                _uiState.value = createErrorStateGroup(current, result)
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun tpyAddAction(): ErrorAddAction {
//        var res = ErrorAddAction.OK
//        val current = uiState.value
//        if (current is AddActionGroupUiState.Error) {
//            _uiState.value = AddActionGroupUiState.Idle(
//                typeAction = current.typeAction,
//                nameAction = current.nameAction,
//                moneyAction = current.moneyAction,
//                data = current.data,
//                category = current.category,
//                description = current.description,
//                allCategory = current.allCategory,
//                menuExpandedType = current.menuExpandedType,
//                menuExpandedCategory = current.menuExpandedCategory,
//                duplication = current.duplication
//            )
//            res =  addAction()
//        }
//        return res
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun addActio1n(): ErrorAddAction {
//        var error = ErrorAddAction.OK
//
//        val current = uiState.value
//
//        if (current is AddActionGroupUiState.Idle) {
//            viewModelScope.launch {
//
//                error = validateIdle(current)
//
//                if (error == ErrorAddAction.OK) {
//
//                    val categoryId = getCategoryIdByName(current.category, current.allCategory)
//
//                    if (categoryId == null)
//                        error = ErrorAddAction.ERROR_ADD_DATA_SERVER
//
//                    _uiState.value = AddActionGroupUiState.Loading(
//                        typeAction = ActionType.NULL,
//                        nameAction = "",
//                        moneyAction = -1,
//                        data = getNowData(),
//                        category = "",
//                        description = "",
//                        allCategory = listOf(),
//                        menuExpandedType = false,
//                        menuExpandedCategory = false,
//                        duplication = false
//                    )
//
//                    if (error == ErrorAddAction.OK) {
//
//                        val timeout = 5000L
//                        val error = withTimeoutOrNull(timeout) {
//                            try {
//                                repository.addActionApi(
//                                    actionName = current.nameAction,
//                                    type = current.typeAction.toInt(),
//                                    value = current.moneyAction,
//                                    date = dataForApi(current.data),
//                                    categoryId = categoryId ?: 1,
//                                    description = current.description,
//                                    duplication = current.duplication
//                                )
//                            } catch (e: Exception) {
//                                Log.e("chooseTypeAndLoad", "Error getting categories", e)
//                                null
//                            }
//                        }.also {
//                            Log.d("chooseTypeAndLoad", "Categories result: $it")
//                        }
//                    }
//                }
//            }
//            Log.d("AddAction error check", error.str)
//            if (error != ErrorAddAction.OK) {
//                _uiState.value = createErrorStateGroup(
//                    current = current,
//                    error = error
//                )
//            }
//        }
//
//        return error
//    }
}
