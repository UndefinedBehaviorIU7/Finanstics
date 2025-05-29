package com.ub.finanstics.presentation.addActionGroup

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ub.finanstics.api.models.Category
import com.ub.finanstics.db.FinansticsDatabase
import com.ub.finanstics.presentation.addAction.ActionType
import com.ub.finanstics.presentation.addAction.ErrorAddAction
import com.ub.finanstics.presentation.addAction.dataForApi
import com.ub.finanstics.presentation.addAction.toInt
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

    private val repository = AddActionGroupRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<AddActionGroupUiState>(
        AddActionGroupUiState.SelectType(typeAction = ActionType.NULL)
    )

    val uiState = _uiState.asStateFlow()

    @Suppress("TooGenericExceptionCaught")
    private fun getNowData(): String {
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
                typeAction = type
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

    private fun createErrorStateGroup(
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
                typeAction = current.typeAction
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
}
