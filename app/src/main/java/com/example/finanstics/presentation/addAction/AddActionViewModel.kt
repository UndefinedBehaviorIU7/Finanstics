package com.example.finanstics.presentation.addAction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanstics.db.Action
import com.example.finanstics.db.FinansticsDatabase
import com.example.finanstics.presentation.calendar.DataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Error(val str: String) {
    NAME("имя"),
    TYPE("тип действия"),
    MONEY("сумма"),
    DATE("дата"),
    CATEGORY("категория"),
    DESCRIPTION("описание")
}

class AddActionViewModel(
    application: Application
) : AndroidViewModel(application) {

    val db = FinansticsDatabase.getDatabase(application)

    private val repository = AddActionRepository(db)
    private val actionDao = db.actionDao()
    private val categoryDao = db.categoryDao()

    private val _uiState = MutableStateFlow<AddActionUiState>(
        AddActionUiState.Idle(
            typeAction = ActionType.NULL,
            nameAction = "",
            moneyAction = -1,
            data = "",
            category = "",
            description = "",
            allCategory = listOf(),
            menuExpandedType = false,
            menuExpandedCategory = false,
        )
    )

    val uiState = _uiState.asStateFlow()

    fun getCategoriesList() {
        viewModelScope.launch {
            val categoryNames = repository.getCategoriesNames()
            _uiState.update { currentState ->
                when (currentState) {
                    is AddActionUiState.Idle -> currentState.copy(allCategory = categoryNames)
                    else -> currentState
                }
            }
        }
    }

    init {
        getCategoriesList()
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
                    menuExpandedType = newMenuExpandedType ?: current.menuExpandedType,
                    menuExpandedCategory = newMenuExpandedCategory ?: current.menuExpandedCategory
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

    private suspend fun validateIdle(state: AddActionUiState.Idle): Error? {
        if (state.nameAction.isBlank()) return Error.NAME
        if (state.typeAction == ActionType.NULL) return Error.TYPE
        if (state.moneyAction <= 0) return Error.MONEY
        if (state.data.isBlank()) return Error.DATE
        if (state.category.isBlank() ||
            categoryDao.getCategoryByName(state.category) == null
            ) return Error.CATEGORY
        if (state.description.isBlank()) return Error.DESCRIPTION

        return null
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
            menuExpandedCategory = current.menuExpandedCategory
        )
    }

    fun addAction() {
        val current = uiState.value
        if (current is AddActionUiState.Idle) {
            viewModelScope.launch {
                val error = validateIdle(current)
                if (error == null) {
                    val data = DataClass.getDataByString(current.data)
                    val action = Action(
                        name = current.nameAction,
                        type = current.typeAction.ordinal,
                        description = current.description,
                        value = current.moneyAction,
                        day = data.getDay(),
                        month = data.getMonth(),
                        year = data.getYear(),
                        categoryId = categoryDao.getCategoryByName(name = current.category)!!.id,
                    )
                    actionDao.insertAction(action)
                    _uiState.value = AddActionUiState.Ok
                } else {
                    _uiState.value = createErrorState(
                        current = current,
                        error = error
                    )
                }
            }
        }
    }
}
