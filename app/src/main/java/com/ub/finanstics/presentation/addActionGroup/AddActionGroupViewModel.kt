package com.ub.finanstics.presentation.addAction

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.calendar.DataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddActionGroupViewModel(
    application: Application
) : AndroidViewModel(application) {

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = AddActionGroupRepository(db, application.applicationContext)
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    private val _uiState = MutableStateFlow<AddActionGroupUiState>(
        AddActionGroupUiState.СhoiceType(typeAction = ActionType.NULL)
    )

    val uiState = _uiState.asStateFlow()

//    fun getCategoriesList() {
//        viewModelScope.launch {
//            val categoryNames = repository.getCategoriesNames()
//            _uiState.update { currentState ->
//                when (currentState) {
//                    is AddActionGroupUiState.Idle -> currentState.copy(allCategory = categoryNames)
//                    else -> currentState
//                }
//            }
//        }
//    }

    fun chooseTypeAndLoad(type: ActionType) {
        _uiState.value = AddActionGroupUiState.СhoiceType(type)

        viewModelScope.launch {
            _uiState.value = AddActionGroupUiState.Loading(
                typeAction = type,
                nameAction = "",
                moneyAction = -1,
                data = "",
                category = "",
                description = "",
                allCategory = listOf(),
                menuExpandedType = false,
                menuExpandedCategory = false,
                duplication = false
            )

            val categories = repository.getCategoriesNames(type.toInt())

            Log.d("categoriesRes", categories.size.toString())

            _uiState.value = AddActionGroupUiState.Idle(
                typeAction = type,
                nameAction = "",
                moneyAction = -1,
                data = "",
                category = "",
                description = "",
                allCategory = categories,
                menuExpandedType = false,
                menuExpandedCategory = false,
                duplication = false,
            )
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
    ): Error {
        if (state.nameAction.isBlank()) return Error.NAME
        if (state.typeAction == ActionType.NULL) return Error.TYPE
        if (state.moneyAction <= 0) return Error.MONEY
        if (state.data.isBlank()) return Error.DATE
        if (state.category.isBlank()) return Error.CATEGORY

        return Error.OK
    }

    fun createErrorStateGroup(
        current: AddActionGroupUiState.Idle,
        error: Error
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

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun addAction() {
//        val current = uiState.value
//        if (current is AddActionUiState.Idle) {
//            viewModelScope.launch {
//                val error = validateIdle(current)
//                if (error == null) {
//                    val data = DataClass.getDataByString(current.data)
//                    val action = Action(
//                        name = current.nameAction,
//                        type = current.typeAction.ordinal,
//                        description = current.description,
//                        value = current.moneyAction,
//                        date = LocalDate.of(data.getYear(), data.getMonth().number, data.getDay()),
//                        categoryId = categoryDao.getCategoryByName(name = current.category)!!.id,
//                        createdAt = "2025-04-22T14:30:00"
//                    )
//                    actionDao.insertAction(action)
//                    _uiState.value = AddActionUiState.Ok
//                } else {
//                    _uiState.value = createErrorState(
//                        current = current,
//                        error = error
//                    )
//                }
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addAction(): Error {
        val current = uiState.value
        var error: Error = Error.UISTATE
        if (current is AddActionGroupUiState.Idle) {
            viewModelScope.launch {
                error = validateIdle(current)
                if (error == Error.OK) {
                    val data = DataClass.getDataByString(current.data)
                    val res = repository.addActionApi(
                        actionName = current.nameAction,
                        type = current.typeAction.toInt(),
                        value = current.moneyAction,
                        date = dataForApi(current.data),
                        categoryId = 1,
                        description = current.description,
                        duplication = current.duplication
                    )
                    if (res == ErrorAddActionGroupApi.Ok) {
                        error = Error.SERVER
                    }
                } else {
                    _uiState.value = createErrorStateGroup(
                        current = current,
                        error = error
                    )
                }
            }
        }
        return error
    }
}
